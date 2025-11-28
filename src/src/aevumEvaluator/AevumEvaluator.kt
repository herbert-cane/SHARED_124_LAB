package src.aevumEvaluator

import src.ast.Expr
import src.token.Token
import src.tokenType.TokenType.*

class AevumEvaluator {

    fun evaluate(expr: Expr): Any? {
        return when (expr) {
            is Expr.Literal -> expr.value
            is Expr.Grouping -> evaluate(expr.expression)
            is Expr.Unary -> evaluateUnary(expr)
            is Expr.Binary -> evaluateBinary(expr)
            else -> null
        }
    }

    private fun evaluateUnary(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            MINUS -> {
                checkUnaryOperand(expr.operator, right)
                val number = right as? Double
                    ?: throw RuntimeErrorHandler.report(expr.operator, "Operand must be a number.")
                -number
            }

            NOT -> !isTruthy(right)

            else -> null
        }
    }
    private fun evaluateBinary(expr: Expr.Binary): Any? {
        val leftOperand = evaluate(expr.left)
        val rightOperand = evaluate(expr.right)

        return when (expr.operator.type) {
            PLUS -> {
                if (leftOperand is Double && rightOperand is Double) {
                    return leftOperand + rightOperand
                }

                if (leftOperand is String && rightOperand is String) {
                    return leftOperand + rightOperand
                }

                throw RuntimeErrorHandler.report(
                    expr.operator,
                    "Operands must be two numbers or two strings."
                )
            }

            MINUS -> {
                checkBinaryOperands(expr.operator, leftOperand, rightOperand)
                val leftVal = leftOperand as Double
                val rightVal = rightOperand as Double
                leftVal - rightVal
            }

            STAR -> {
                checkBinaryOperands(expr.operator, leftOperand, rightOperand)
                (leftOperand as Double) * (rightOperand as Double)
            }

            SLASH -> {
                checkBinaryOperands(expr.operator, leftOperand, rightOperand)
                val rightVal = rightOperand as Double
                if (rightVal == 0.0) {
                    throw RuntimeErrorHandler.report(expr.operator, "Division by zero.")
                }
                (leftOperand as Double) / rightVal
            }

            GREATER -> compareNumbers(expr.operator, leftOperand, rightOperand) { a, b -> a > b }
            GREATER_EQUAL -> compareNumbers(expr.operator, leftOperand, rightOperand) { a, b -> a >= b }
            LESS -> compareNumbers(expr.operator, leftOperand, rightOperand) { a, b -> a < b }
            LESS_EQUAL -> compareNumbers(expr.operator, leftOperand, rightOperand) { a, b -> a <= b }
            EQUAL_EQUAL -> isEqual(leftOperand, rightOperand)
            NOT_EQUAL -> !isEqual(leftOperand, rightOperand)

            else -> null
        }
    }

    private fun compareNumbers(operator: Token, leftOperand: Any?, rightOperand: Any?, compare:(Double, Double)
    -> Boolean): Boolean {
        val left = leftOperand as? Double
            ?: throw RuntimeErrorHandler.report(operator, "Operands must be two numbers.")

        val right = rightOperand as? Double
            ?: throw RuntimeErrorHandler.report(operator, "Operands must be two numbers.")

        return compare(left, right)
    }

    private fun checkUnaryOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeErrorHandler.report(operator, "Operand must be a number.")
    }

    private fun checkBinaryOperands(operator: Token, leftOperand: Any?, rightOperand: Any?) {
        if (leftOperand is Double && rightOperand is Double) return
        throw RuntimeErrorHandler.report(operator, "Operands must be numbers.")
    }

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        if (value is Boolean) return value

        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false

        return a == b
    }
}