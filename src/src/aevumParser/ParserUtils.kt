package src.aevumParser

import src.error.ParserErrorHandler
import src.token.Token
import src.tokenType.TokenType

class ParserUtilities(private val tokens: List<Token>) {
    private var current = 0

    fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw ParserErrorHandler.report(peek(), message)
    }

    fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    fun peek(): Token = tokens[current]

    fun previous(): Token = tokens[current - 1]
}