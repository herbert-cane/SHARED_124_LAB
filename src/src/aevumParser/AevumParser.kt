package src.aevumParser

import src.ast.Expr
import src.token.Token
import src.tokenType.TokenType.*

/**
 * Recursive Descent Parser for the Aevum programming language.
 *
 * This parser transforms a sequence of tokens into an Abstract Syntax Tree (AST)
 * according to the language's context-free grammar. It uses a top-down approach
 * where each grammar production rule maps to a recursive method.
 *
 * Grammar:
 * expression → equality
 * equality   → comparison ( ( "!=" | "==" ) comparison )*
 * comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )*
 * term       → factor ( ( "-" | "+" ) factor )*
 * factor     → unary ( ( "/" | "*" ) unary )*
 * unary      → ( "!" | "-" ) unary | primary
 * primary    → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER
 */
class AevumParser(private val tokens: List<Token>) {
    // Delegate utility operations to the ParserUtilities class
    private val utils = ParserUtilities(tokens)

    /**
     * Main parsing entry point - converts tokens into an AST.
     *
     * @return The root expression node of the AST, or null if parsing failed
     */
    fun parse(): Expr? {
        return try {
            // If we're at the end with no tokens, it's an error (empty input)
            if (utils.isAtEnd() || (tokens.size == 1 && tokens[0].type == EOF)) {
                throw utils.error(utils.peek(), "Expect expression.")
            }

            // Start parsing from the highest-level expression rule
            val expr = expression()

            // Ensure we consumed all input tokens (no extra tokens remaining)
            if (!utils.isAtEnd()) {
                throw utils.error(utils.peek(), "Unexpected token after expression.")
            }

            expr
        } catch (_: ParserUtilities.ParseError) {
            // Return null to indicate parsing failure (error already printed)
            null
        }
    }

    /**
     * expression → equality
     *
     * The root of all expressions. Currently maps directly to equality
     * but provides a hook for future expression types.
     */
    private fun expression(): Expr {
        val expr = equality()

        // Special case: reject sequences of identifiers like "this is nonsense"
        // If we just parsed a variable and the next token is also an identifier,
        // this indicates invalid syntax rather than a valid expression
        if (!utils.isAtEnd() && utils.peek().type == IDENTIFIER && expr is Expr.Variable) {
            throw utils.error(utils.previous(), "Expect expression.")
        }

        return expr
    }

    /**
     * equality → comparison ( ( "!=" | "==" ) comparison )*
     *
     * Handles equality operators with lowest precedence.
     * The * symbol means zero or more repetitions (left-associative).
     */
    private fun equality(): Expr {
        // Start with a comparison (higher precedence)
        var expr = comparison()

        // Keep matching equality operators while they appear
        while (utils.match(NOT_EQUAL, EQUAL_EQUAL)) {
            val operator = utils.previous()  // The matched operator token
            val right = comparison()         // Parse the right-hand side
            // Build binary expression: left operator right
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )*
     *
     * Handles comparison operators with higher precedence than equality.
     */
    private fun comparison(): Expr {
        var expr = term()

        while (utils.match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = utils.previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * term → factor ( ( "-" | "+" ) factor )*
     *
     * Handles addition and subtraction operators.
     */
    private fun term(): Expr {
        var expr = factor()

        while (utils.match(PLUS, MINUS)) {
            val operator = utils.previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * factor → unary ( ( "/" | "*" ) unary )*
     *
     * Handles multiplication and division operators with higher precedence.
     */
    private fun factor(): Expr {
        var expr = unary()

        while (utils.match(STAR, SLASH)) {
            val operator = utils.previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * unary → ( "!" | "-" ) unary | primary
     *
     * Handles unary operators (logical NOT and negation).
     * These have the highest precedence of all operators.
     */
    private fun unary(): Expr {
        // Check for unary operators at the beginning
        if (utils.match(NOT, MINUS)) {
            val operator = utils.previous()
            val right = unary()  // Recursively parse the operand
            return Expr.Unary(operator, right)
        }
        // No unary operator found, move to primary expressions
        return primary()
    }

    /**
     * primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER
     *
     * Handles the most basic expressions - literals, grouped expressions, and variables.
     * These form the leaves of the AST.
     */
    private fun primary(): Expr {
        // Boolean literals
        if (utils.match(FALSE)) return Expr.Literal(false)
        if (utils.match(TRUE)) return Expr.Literal(true)

        // Null literal
        if (utils.match(NIL)) return Expr.Literal(null)

        // Number and string literals
        if (utils.match(NUMBER, STRING)) {
            return Expr.Literal(utils.previous().literal)
        }

        // Parenthesized expressions (grouping)
        if (utils.match(LEFT_PAREN)) {
            val expr = expression()  // Recursively parse the inner expression
            utils.consume(RIGHT_PAREN, "Expect ')' after expression.")  // Ensure closing parenthesis
            return Expr.Grouping(expr)
        }

        // Variable references (identifiers)
        if (utils.match(IDENTIFIER)) {
            return Expr.Variable(utils.previous())
        }

        // If none of the above matched, we have a syntax error
        throw utils.error(utils.peek(), "Expect expression.")
    }
}