package src.token
import src.tokenType.TokenType

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any? = null,
    val line: Int,
    val description: String = ""
) {
    override fun toString(): String {
        val literalStr = when (literal) {
            null -> "null"
            is String -> "\"$literal\""
            else -> literal.toString()
        }
        return "Token(type=$type, lexeme=$lexeme, literal=$literalStr, line=$line)"
    }
}