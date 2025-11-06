package src.aevumEvaluator

import src.token.Token

object RuntimeErrorHandler {
    fun report(token: Token, message: String): RuntimeError {
//        val location = if (token.lexeme.isNotEmpty()) "at '${token.lexeme}'" else ""
        println("[line ${token.line}] Runtime error: $message")

        return RuntimeError(message)
    }

    class RuntimeError(message: String): RuntimeException(message)
}