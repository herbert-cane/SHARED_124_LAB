package src.error

import src.token.Token

object ParserErrorHandler {

    class ParseError: RuntimeException()

    fun report(token: Token, message: String): ParseError {
        val errorLocation = if (token.lexeme.isEmpty()) "at end" else "at '${token.lexeme}'"
        System.err.println("[line ${token.line}] Error $errorLocation: $message")

        return ParseError()
    }

}