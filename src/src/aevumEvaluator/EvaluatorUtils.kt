package src.aevumEvaluator

import src.token.Token

object EvaluatorUtils {

    fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeErrorHandler.report(operator, "Operand must be a number.")
    }

    fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeErrorHandler.report(operator, "Operands must be numbers.")
    }

    fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false
        return a == b
    }

    fun stringify(obj: Any?): String {
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

    // Helper to count arguments in the nested Pair structure
    // Pair(1, Pair(2, 3)) -> returns 3
    fun countArgs(args: Any?): Int {
        if (args == null) return 0
        if (args is Pair<*, *>) {
            return 1 + countArgs(args.second)
        }
        return 1
    }
}