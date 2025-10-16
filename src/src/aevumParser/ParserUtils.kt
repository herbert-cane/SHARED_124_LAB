package src.aevumParser

import src.token.Token
import src.tokenType.TokenType

/**
 * Utility functions for the recursive descent parser.
 *
 * These functions provide common parsing operations like lookahead,
 * token matching, and error handling that are used across multiple
 * parsing methods.
 */
class ParserUtilities(private val tokens: List<Token>) {
    private var current = 0

    /**
     * Checks if the current token matches any of the given types.
     * If it matches, consumes the token and returns true.
     *
     * @param types The token types to check for
     * @return True if a match was found and token consumed, false otherwise
     */
    fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    /**
     * Expects and consumes a token of the specified type.
     * Throws an error if the current token doesn't match.
     *
     * @param type The expected token type
     * @param message Error message to display if expectation fails
     * @return The consumed token
     * @throws ParseError if the token doesn't match expected type
     */
    fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    /**
     * Checks if the current token is of the specified type without consuming it.
     *
     * @param type The token type to check for
     * @return True if current token matches the type, false otherwise
     */
    fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    /**
     * Advances to the next token and returns the previous one.
     *
     * @return The token that was just advanced past
     */
    fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    /**
     * Checks if we've consumed all tokens in the input.
     *
     * @return True if we're at the end of the token stream
     */
    fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    /**
     * Returns the current token without consuming it.
     *
     * @return The current lookahead token
     */
    fun peek(): Token = tokens[current]

    /**
     * Returns the most recently consumed token.
     *
     * @return The previous token
     */
    fun previous(): Token = tokens[current - 1]

    /**
     * Reports a parsing error and returns a ParseError for unwinding the call stack.
     *
     * @param token The token where the error occurred
     * @param message Description of the error
     * @return A ParseError instance to throw
     */
    fun error(token: Token, message: String): ParseError {
        println("[line ${token.line}] Error at '${token.lexeme}': $message")
        return ParseError()
    }

    /**
     * Exception class for parser errors.
     * Used for control flow to unwind the recursive call stack when errors occur.
     */
    class ParseError : RuntimeException()
}