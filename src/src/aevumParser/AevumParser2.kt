package src.aevumParser

import src.ast.Expr
import src.ast.Stmt
import src.error.ParserErrorHandler
import src.token.Token
import src.tokenType.TokenType.*

class AevumParser2(private val tokens: List<Token>) {
    private val utils = ParserUtilities(tokens)

    // -------------------------------------------------------------------------
    // 1. Entry Point
    // -------------------------------------------------------------------------

    // Now returns a list of statements (a full program) instead of one expression
    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()

        while (!utils.isAtEnd()) {
            val decl = declaration()
            if (decl != null) {
                statements.add(decl)
            }
        }

        return statements
    }

    // -------------------------------------------------------------------------
    // 2. Statement Parsing
    // -------------------------------------------------------------------------

    private fun declaration(): Stmt? {
        return try {
            if (utils.match(VAR)) varDeclaration() else statement()
        } catch (error: ParserErrorHandler.ParseError) {
            synchronize()
            null
        }
    }

    private fun varDeclaration(): Stmt {
        val name = utils.consume(IDENTIFIER, "Expect variable name.")

        var initializer: Expr? = null
        if (utils.match(EQUAL)) {
            initializer = expression()
        }

        utils.consume(SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.Var(name, initializer)
    }

    private fun statement(): Stmt {
        if (utils.match(PRINT)) return printStatement()
        if (utils.match(LEFT_BRACE)) return Stmt.Block(block())
        return expressionStatement()
    }

    private fun printStatement(): Stmt {
        val value = expression()
        utils.consume(SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    private fun block(): List<Stmt> {
        val statements = mutableListOf<Stmt>()

        while (!utils.check(RIGHT_BRACE) && !utils.isAtEnd()) {
            val decl = declaration()
            if (decl != null) statements.add(decl)
        }

        utils.consume(RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        utils.consume(SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    // -------------------------------------------------------------------------
    // 3. Expression Parsing (Updated for Assignment)
    // -------------------------------------------------------------------------

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        // Parse the left-hand side (which might be the assignment target)
        val expr = equality()

        if (utils.match(EQUAL)) {
            val equals = utils.previous()
            val value = assignment() // Recursive to handle a = b = 5

            if (expr is Expr.Variable) {
                return Expr.Assign(expr.name, value)
            }

            // We report the error but don't throw it, effectively ignoring the assignment
            // to keep the parser going if possible.
            ParserErrorHandler.report(equals, "Invalid assignment target.")
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

    // -------------------------------------------------------------------------
    // 4. Error Recovery
    // -------------------------------------------------------------------------

    private fun synchronize() {
        utils.advance()

        while (!utils.isAtEnd()) {
            if (utils.previous().type == SEMICOLON) return

            when (utils.peek().type) {
                CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return
                else -> utils.advance()
            }
        }
    }
}