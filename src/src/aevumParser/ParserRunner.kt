package src.aevumParser

import src.ast.Expr
import src.error.ParserErrorHandler
import src.token.Token


object ParserRunner {
    fun run(tokens: List<Token>): Expr? {
        val parser = AevumParser(tokens)
        return try {
            parser.parse()

        } catch (_: ParserErrorHandler.ParseError) {
            // Return null to indicate parsing failure (error already printed)
            null
        }
    }

}