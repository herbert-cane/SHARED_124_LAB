import src.aevumEvaluator.AevumEvaluator
import src.aevumEvaluator.RuntimeErrorHandler
import src.aevumScanner.AevumScanner
import src.aevumParser.AevumParser
import src.token.Token
import src.error.ParserErrorHandler
import java.util.Scanner as InputScanner

fun main() {
    val inputScanner = InputScanner(System.`in`)
    val scanner = AevumScanner()
    // val printer = AstPrinter()
    val evaluator = AevumEvaluator()

    println("Welcome to Aevum REPL. Type your code below:")
    println("Type 'exit' to quit.")

    var lineNumber = 1

    while (true) {
        print("> ")
        if (!inputScanner.hasNextLine()) break // Handle Ctrl+D/EOF
        val line = inputScanner.nextLine().trim()

        if (line.equals("exit", ignoreCase = true)) break
        if (line.isBlank()) continue

        // 1. Scan
        val tokens: List<Token> = scanner.scanLine(line, lineNumber)

        if (tokens.isNotEmpty()) {
            // 2. Parse
            val parser = AevumParser(tokens)

            try {
                val expression = parser.parse()

                if (expression != null) {
                    // 3. Evaluate
                    // We wrap this in a try-catch to handle the RuntimeErrors
                    // thrown by your RuntimeErrorHandler.report()
                    try {
                        val result = evaluator.evaluate(expression)
                        println(stringify(result))
                    } catch (e: RuntimeErrorHandler.RuntimeError) {
                        // The error message is already printed inside
                        // RuntimeErrorHandler.report(), so we just catch it here
                        // to prevent the app from crashing.
                    }
                }

            } catch (error: ParserErrorHandler.ParseError) {
                println("[line $lineNumber] Error: ${error.message}")
            }
        } else {
            // No meaningful tokens
            // println("[line $lineNumber] Error at end: Expect expression.")
        }

        lineNumber++
    }

    println("Goodbye!")
}

/**
 * Helper function to format values according to Lab 3 specs.
 * Handles integer-like Doubles and nil values.
 */
fun stringify(obj: Any?): String {
    if (obj == null) return "nil"

    if (obj is Double) {
        var text = obj.toString()
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length - 2)
        }
        return text
    }

    return obj.toString()
}