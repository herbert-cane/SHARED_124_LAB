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

    fun parse(): Stmt.Program {
        val body = parseStatementsRecursively()
        return Stmt.Program(body)
    }

    /**
     * Builds a Binary Tree of Statements.
     * 1. Parse the current statement (Left Child).
     * 2. Recursively parse the rest (Right Child).
     * 3. Combine them into a Sequence node.
     */
    private fun parseStatementsRecursively(): Stmt? {
        if (utils.isAtEnd()) {
            return null
        }

        val first = declaration()

        // If the current declaration failed, try to recover/continue
        if (first == null) {
            return parseStatementsRecursively()
        }

        val rest = parseStatementsRecursively() ?: return first

        // If there is no code after this, just return the single statement

        // Connect them: Sequence(Left, Right)
        return Stmt.Sequence(first, rest)
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
        if (utils.match(LEFT_BRACE)) return block()
        return expressionStatement()
    }

    private fun printStatement(): Stmt {
        val value = expression()
        utils.consume(SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    // [CHANGE] Block now returns a Tree of Stmts
    private fun block(): Stmt {
        val body = parseBlockRecursively()
        utils.consume(RIGHT_BRACE, "Expect '}' after block.")
        return Stmt.Block(body)
    }

    private fun parseBlockRecursively(): Stmt? {
        if (utils.check(RIGHT_BRACE) || utils.isAtEnd()) {
            return null
        }

        val first = declaration()

        if (first == null) {
            return parseBlockRecursively()
        }

        val rest = parseBlockRecursively()

        if (rest == null) {
            return first
        }

        return Stmt.Sequence(first, rest)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        utils.consume(SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    // ... (All expression parsing and synchronize methods remain exactly the same) ...
    // Copy them from your previous file.
    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = equality()

        if (utils.match(EQUAL)) {
            val equals = utils.previous()
            val value = assignment()

            if (expr is Expr.Variable) {
                return Expr.Assign(expr.name, value)
            }

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