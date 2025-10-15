package src.parser

import src.token.Token
import src.tokenType.TokenType

class ParseError(message: String) : RuntimeException(message)

class AevumParser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Expr? {
        return try {
            expression()
        }
        catch (error: ParseError) {
            null
        }
    }

    private fun expression(): Expr = equality()

    private fun equality(): Expr {
        var expr = comparison()

        while ( match(TokenType.NOT_EQUAL, TokenType.EQUAL_EQUAL)) {
            val operator = prev()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val operator = prev()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = prev()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = prev()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            val operator = prev()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.TRUE)) return Expr.Literal(true)
        if (match(TokenType.FALSE)) return Expr.Literal(false)
        if (match(TokenType.NIL)) return Expr.Literal(null)

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(prev().literal)
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect expression.")

    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                next()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return next()
        throw error(peek(), message)
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun next(): Token {
        if (!isAtEnd()) current++
        return prev()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF
    private fun peek(): Token = tokens[current]
    private fun prev(): Token = tokens[current -1]

    private fun error(token: Token, message: String): ParseError {
        if (token.type == TokenType.EOF) {
            println("[line ${token.line}] Error at end: $message")

        } else {
            println("[line ${token.line}] Error at '${token.lexeme}': $message")
        }

        return ParseError(message)
    }
}