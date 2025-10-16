package src.parser

class AstPrinter {
    fun print(expr: Expr): String {
        return when (expr) {
            is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
            is Expr.Grouping -> parenthesize("group", expr.expression)
            is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)
            is Expr.Literal -> literalToString(expr.value)
        }
    }

    private fun parenthesize(name: String, vararg expressions: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in expressions) {
            builder.append(" ").append(print(expr))
        }
        builder.append(")")
        return builder.toString()
    }

    private fun literalToString(value: Any?): String {
        return when (value) {
            null -> "nil"
            is String -> value.removeSurrounding("\"")
            else -> value.toString()
        }
    }

}