package src.aevumEvaluator

import src.ast.Expr
import src.ast.Stmt
import src.aevumEnvironment.Environment
import src.tokenType.TokenType.*

class AevumEvaluator2 {
    private var environment = Environment()

    // Accept the Root Program Node
    fun interpret(program: Stmt.Program) {
        try {
            if (program.body != null) {
                execute(program.body)
            }
        } catch (error: RuntimeErrorHandler.RuntimeError) {
            // catch logic
        }
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            // The Sequence Logic (Traversing the tree)
            is Stmt.Sequence -> {
                execute(stmt.first) // Execute Left Child
                execute(stmt.next)  // Execute Right Child
            }

            // Program and Block now just unwrap the body
            is Stmt.Program -> if (stmt.body != null) execute(stmt.body)
            is Stmt.Block -> {
                if (stmt.body != null) {
                    executeBlock(stmt.body, Environment(environment))
                }
            }
            is Stmt.Expression -> evaluate(stmt.expression)
            is Stmt.Print -> {
                val value = evaluate(stmt.expression)
                println(EvaluatorUtils.stringify(value))
            }
            is Stmt.Var -> {
                var value: Any? = null
                if (stmt.initializer != null) {
                    value = evaluate(stmt.initializer)
                }
                environment.define(stmt.name.lexeme, value)
            }
        }
    }

    // Updated Block Execution
    private fun executeBlock(body: Stmt, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            execute(body) // This will trigger the Sequence traversal recursively
        } finally {
            this.environment = previous
        }
    }
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
                // [Use Helper]
                EvaluatorUtils.checkNumberOperand(expr.operator, right)
                -(right as Double)
            }
            NOT -> !EvaluatorUtils.isTruthy(right) // [Use Helper]
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

                // If EITHER is a string, concatenate them as strings
                if (left is String || right is String) {
                    // [Use Helper]
                    return EvaluatorUtils.stringify(left) + EvaluatorUtils.stringify(right)
                }

                throw RuntimeErrorHandler.report(expr.operator, "Operands must be two numbers or two strings.")
            }
            MINUS -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) - (right as Double)
            }
            STAR -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) * (right as Double)
            }
            SLASH -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                val r = (right as Double)
                if (r == 0.0) throw RuntimeErrorHandler.report(expr.operator, "Division by zero.")
                (left as Double) / r
            }
            GREATER -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) > (right as Double)
            }
            GREATER_EQUAL -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) >= (right as Double)
            }
            LESS -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }
            LESS_EQUAL -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) <= (right as Double)
            }
            EQUAL_EQUAL -> EvaluatorUtils.isEqual(left, right)
            NOT_EQUAL -> !EvaluatorUtils.isEqual(left, right)
            else -> null
        }
    }
}