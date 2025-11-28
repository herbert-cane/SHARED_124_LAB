package src.error  // Note the package is 'src.error'

import src.token.Token
import src.tokenType.TokenType

/**
 * Singleton object to handle parser errors globally.
 */
object ParserErrorHandler {

    /**
     * The exception class used for control flow to unwind the stack.
     */
    class ParseError : RuntimeException()

    /**
     * Reports an error and returns the exception to be thrown.
     */
    fun report(token: Token, message: String): ParseError {
        if (token.type == TokenType.EOF) {
            println("[line ${token.line}] Error at end: $message")
        } else {
            println("[line ${token.line}] Error at '${token.lexeme}': $message")
        }
        // Return the exception so the caller can throw it
        return ParseError()
    }
}