package src.aevumParser

import src.ast.Expr
import src.error.ParserErrorHandler
import src.token.Token
import src.tokenType.TokenType.*

class AevumParser(private val tokens: List<Token>) {
    private val utils = ParserUtilities(tokens)

    fun parse(): Expr? {
        // Ensure we don't crash on empty input
        if (utils.isAtEnd() && tokens.isEmpty()) {
            return null
        }

        // Check for EOF as the only token
        if (tokens.size == 1 && tokens[0].type == EOF) {
            throw ParserErrorHandler.report(utils.peek(), "Expect expression.")
        }

        val expr = expression()

        if (!utils.isAtEnd()) {
            throw ParserErrorHandler.report(utils.peek(), "Unexpected token after expression.")
        }

        return expr
    }

    private fun expression(): Expr {
        val expr = equality()

        // Block sequences like "var identifier" in expression mode
        if (!utils.isAtEnd() && utils.peek().type == IDENTIFIER && expr is Expr.Variable) {
            throw ParserErrorHandler.report(utils.previous(), "Expect expression.")
        }

        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()
        while (utils.match(NOT_EQUAL, EQUAL_EQUAL)) {
            val operator = utils.previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = term()
        while (utils.match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = utils.previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (utils.match(PLUS, MINUS)) {
            val operator = utils.previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (utils.match(STAR, SLASH)) {
            val operator = utils.previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (utils.match(NOT, MINUS)) {
            val operator = utils.previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        if (utils.match(FALSE)) return Expr.Literal(false)
        if (utils.match(TRUE)) return Expr.Literal(true)
        if (utils.match(NIL)) return Expr.Literal(null)

        if (utils.match(NUMBER, STRING)) {
            return Expr.Literal(utils.previous().literal)
        }

        if (utils.match(LEFT_PAREN)) {
            val expr = expression()
            utils.consume(RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        if (utils.match(IDENTIFIER)) {
            return Expr.Variable(utils.previous())
        }

        throw ParserErrorHandler.report(utils.peek(), "Expect expression.")
    }
}