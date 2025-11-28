package src.aevumEvaluator

import src.ast.Expr
import src.ast.Stmt
import src.environment.Environment
import src.token.Token
import src.tokenType.TokenType.*

class AevumEvaluator2 {
    // The current environment (scope). Starts with the global scope.
    private var environment = Environment()

    // Entry point for a list of statements (Program)
    fun interpret(statements: List<Stmt>) {
        try {
            for (statement in statements) {
                execute(statement)
            }
        } catch (error: RuntimeErrorHandler.RuntimeError) {
            // The RuntimeErrorHandler.report() method already printed the error message
            // before throwing the exception. We just catch it here to stop execution.
        }
    }

    // execute() handles Statements (Action)
    private fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Expression -> evaluate(stmt.expression)
            is Stmt.Print -> {
                val value = evaluate(stmt.expression)
                println(stringify(value))
            }
            is Stmt.Var -> {
                var value: Any? = null
                if (stmt.initializer != null) {
                    value = evaluate(stmt.initializer)
                }
                environment.define(stmt.name.lexeme, value)
            }
            is Stmt.Block -> {
                executeBlock(stmt.statements, Environment(environment))
            }
        }
    }

    // executeBlock() handles nested scopes
    private fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            for (statement in statements) {
                execute(statement)
            }
        } finally {
            this.environment = previous
        }
    }

    // evaluate() handles Expressions (Values)
    private fun evaluate(expr: Expr): Any? {
        return when (expr) {
            is Expr.Literal -> expr.value
            is Expr.Grouping -> evaluate(expr.expression)
            is Expr.Unary -> evaluateUnary(expr)
            is Expr.Binary -> evaluateBinary(expr)
            is Expr.Variable -> environment.get(expr.name)
            is Expr.Assign -> {
                val value = evaluate(expr.value)
                environment.assign(expr.name, value)
                value
            }
        }
    }

    private fun evaluateUnary(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }
            NOT -> !isTruthy(right)
            else -> null
        }
    }

    private fun evaluateBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            PLUS -> {
                // If both are numbers, add them
                if (left is Double && right is Double) {
                    return left + right
                }

                // [FIX] If EITHER is a string, concatenate them as strings
                // We use stringify() to ensure nil/numbers are formatted correctly
                if (left is String || right is String) {
                    return stringify(left) + stringify(right)
                }

                throw RuntimeErrorHandler.report(expr.operator, "Operands must be two numbers or two strings.")
            }
            MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) - (right as Double)
            }
            STAR -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) * (right as Double)
            }
            SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                val r = (right as Double)
                if (r == 0.0) throw RuntimeErrorHandler.report(expr.operator, "Division by zero.")
                (left as Double) / r
            }
            GREATER -> { checkNumberOperands(expr.operator, left, right); (left as Double) > (right as Double) }
            GREATER_EQUAL -> { checkNumberOperands(expr.operator, left, right); (left as Double) >= (right as Double) }
            LESS -> { checkNumberOperands(expr.operator, left, right); (left as Double) < (right as Double) }
            LESS_EQUAL -> { checkNumberOperands(expr.operator, left, right); (left as Double) <= (right as Double) }
            EQUAL_EQUAL -> isEqual(left, right)
            NOT_EQUAL -> !isEqual(left, right)
            else -> null
        }
    }

    // Helpers
    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeErrorHandler.report(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeErrorHandler.report(operator, "Operands must be numbers.")
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false
        return a == b
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"
        if (obj is Double) {
            val text = obj.toString()
            if (text.endsWith(".0")) {
                return text.removeSuffix(".0")
            }
            return text
        }
        return obj.toString()
    }
}