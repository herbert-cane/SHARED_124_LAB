package src.aevumParser

import src.ast.Expr
import src.ast.Stmt
import src.error.ParserErrorHandler
import src.token.Token
import src.tokenType.TokenType.*

class AevumParser2(tokens: List<Token>) {
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

        val first = declaration() ?: return parseStatementsRecursively()

        // If the current declaration failed, try to recover/continue

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
            if (utils.match(FUN)) return function("function")
            if (utils.match(VAR)) return varDeclaration()

            return statement()

        } catch (_: ParserErrorHandler.ParseError) {
            utils.synchronize()
            null
        }
    }

    private fun function(declarationType: String) : Stmt {
        val name = utils.consume(IDENTIFIER, "Except $declarationType name.")
        utils.consume(LEFT_PAREN, "Expect '(' after $declarationType name.")

        // recursive expression tree for parameters
        var parameters: Expr? = null
        if (!utils.check(RIGHT_PAREN)) {
            parameters = parseParameterList()
        }

        utils.consume(RIGHT_PAREN, "Expect ')' after parameters.")
        utils.consume(LEFT_BRACE, "Expect '}' before $declarationType body.")

        val body = block()  // reuse block parsing

        return Stmt.Function(name, parameters, body)
    }

    // === [Lab 5] Helper to parse parameters recursively ===
    private fun parseParameterList() : Expr {
        val parameterName = utils.consume(IDENTIFIER, "Expect parameter name.")
        val head = Expr.Variable(parameterName)

        if (utils.match(COMMA)) {
            val tail = parseParameterList()

            return Expr.Binary(head, utils.previous(), tail)
        }

        return head
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
        if (utils.match(FOR)) return forStatement()
        if (utils.match(WHILE)) return whileStatement()
        if (utils.match(RETURN)) return returnStatement()
        if (utils.match(IF)) return ifStatement()
        if (utils.match(PRINT)) return printStatement()
        if (utils.match(LEFT_BRACE)) return block()
        return expressionStatement()
    }
    // [Lab 5] For Statement
    private fun forStatement(): Stmt {
        utils.consume(LEFT_PAREN, "Expect '(' after 'for'.")

        // 1. Parse Initializer
        // (var i = 0;)
        val initializer = if (utils.match(SEMICOLON)) {
            null
        } else if (utils.match(VAR)) {
            varDeclaration()
        } else {
            expressionStatement()
        }

        // 2. Parse Condition
        // (i < 10;)
        var condition: Expr? = null
        if (!utils.check(SEMICOLON)) {
            condition = expression()
        }
        utils.consume(SEMICOLON, "Expect ';' after loop condition.")

        // 3. Parse Increment
        // (i = i + 1)
        var increment: Expr? = null
        // Check if next token is NOT ')', then parse.
        if (!utils.check(RIGHT_PAREN)) {
            increment = expression()
        }
        utils.consume(RIGHT_PAREN, "Expect ')' after for clauses.")

        // 4. Parse Body
        var body = statement()

        // --- DESUGARING LOGIC ---

        // A. Handle Increment
        // If there is an increment, it executes after the body in every iteration.
        // We create a sequence: { body; increment; }
        if (increment != null) {
            body = Stmt.Sequence(
                body,
                Stmt.Expression(increment)
            )
        }

        // B. Handle Condition
        // If condition is null, it loops forever (true).
        // while (condition) { body }
        if (condition == null) {
            condition = Expr.Literal(true)
        }
        body = Stmt.While(condition, body)

        // C. Handle Initializer
        // If there is an initializer, it runs once before the loop.
        // { initializer; while(...) }
        if (initializer != null) {
            body = Stmt.Sequence(initializer, body)
        }

        // Wrap everything in a Block so the initializer variable (e.g. 'var i')
        // is scoped locally to the loop and doesn't leak out.
        return Stmt.Block(body)
    }

    // === [Lab 5] While Statement ===
    private fun whileStatement() : Stmt {
        utils.consume(LEFT_PAREN, "Expect '(' after 'while'.")
        val condition = expression()
        utils.consume(RIGHT_PAREN, "Expect ')' after condition.")
        val body = statement()

        return Stmt.While(condition, body)
    }

    // === [Lab 5] If Statement ===
    private fun ifStatement() : Stmt {
        utils.consume(LEFT_PAREN, "Expect '(' after 'if'.")
        val condition = expression()
        utils.consume(RIGHT_PAREN, "Expect ')' after if condition.")

        val thenBranch = statement()
        var elseBranch: Stmt? = null

        if (utils.match(ELSE)) {
            elseBranch = statement()
        }

        return Stmt.If(condition, thenBranch, elseBranch)
    }
    // === [Lab 5] Return Statement ===
    private fun returnStatement() : Stmt {
        val keyword = utils.previous()
        var value : Expr? = null
        if (!utils.check(SEMICOLON)) {
            value = expression()
        }
        utils.consume(SEMICOLON, "Expect ';' after return value.")
        return Stmt.Return(keyword, value)
    }

    private fun printStatement(): Stmt {
        val value = expression()
        utils.consume(SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    // === [LAB 4] Block now returns a Tree of Statements ===
    private fun block(): Stmt {
        val body = parseBlockRecursively()
        utils.consume(RIGHT_BRACE, "Expect '}' after block.")
        return Stmt.Block(body)
    }

    private fun parseBlockRecursively(): Stmt? {
        if (utils.check(RIGHT_BRACE) || utils.isAtEnd()) {
            return null
        }

        val first = declaration() ?: return parseBlockRecursively()

        val rest = parseBlockRecursively() ?: return first

        return Stmt.Sequence(first, rest)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        utils.consume(SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = or()

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

    // === [Lab 5] Logical OR ===
    private fun or() : Expr {
        var expr = and()
        while (utils.match(OR)) {
            val operator = utils.previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    // === [Lab 5] Logical AND ===
    private fun and(): Expr {
        var expr = equality()
        while (utils.match(AND)) {
            val operator = utils.previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    // === [Lab 5] Function Call ===
    private fun call(): Expr {
        var expr = primary()
        while (true) {
            // If we see '(', it's a function call
            if (utils.match(LEFT_PAREN)) {
                expr = finishFunctionCall(expr)
            } else {
                break
            }
        }
        return expr
    }

    // === [Lab 5] Helper to parse arguments recursively ===
    private fun finishFunctionCall(callee: Expr): Expr {
        var arguments: Expr? = null
        if (!utils.check(RIGHT_PAREN)) {
            arguments = parseArgumentList()
        }
        val paren = utils.consume(RIGHT_PAREN, "Expect ')' after arguments.")
        return Expr.FunctionCall(callee, paren, arguments)
    }

    // === [Lab 5] Recursive helper for arguments ===
    private fun parseArgumentList(): Expr {
        val head = expression()
        if (utils.match(COMMA)) {
            val operator = utils.previous()
            val tail = parseArgumentList()
            return Expr.Binary(head, operator, tail)
        }
        return head
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
        return call()
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