import src.aevumScanner.AevumScanner
import src.aevumParser.AevumParser
import src.ast.AstPrinter
import src.token.Token
import src.error.ParserErrorHandler
import java.util.Scanner as InputScanner

fun main() {
    val inputScanner = InputScanner(System.`in`)
    val scanner = AevumScanner()
    val printer = AstPrinter()

    println("Welcome to Aevum REPL. Type your code below:")
    println("Type 'exit' to quit.")

    var lineNumber = 1

    while (true) {
        print("> ")
        val line = inputScanner.nextLine().trim()

        if (line.equals("exit", ignoreCase = true)) break
        if (line.isBlank()) continue

        // Scan the line into tokens
        val tokens: List<Token> = scanner.scanLine(line, lineNumber)

        // Filter out EOF for display and check if we have any meaningful tokens
        val meaningfulTokens = tokens.filter {
            it.type != src.tokenType.TokenType.EOF &&
                    !it.lexeme.isBlank()
        }

        // Print tokens only if we have meaningful content (not just comments)
        if (meaningfulTokens.isNotEmpty()) {
            meaningfulTokens.forEach { println(it) }
        }

        // Parse the tokens into an AST - but only if we have meaningful tokens
        if (meaningfulTokens.isNotEmpty()) {
            val parser = AevumParser(tokens)

            try {
                val expression = parser.parse()

                // Print the AST if parsing was successful
                if (expression != null) {
                    println(printer.print(expression))
                }

            } catch (error: ParserErrorHandler.ParseError) {
                println("[line $lineNumber] Error: ${error.message}")
            }

            // If expression is null, the parser already printed the error
        } else {
            // No meaningful tokens (just comments or whitespace)
            println("[line $lineNumber] Error at end: Expect expression.")
        }

        lineNumber++
    }

    println("Goodbye!")
}