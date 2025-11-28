import src.aevumScanner.AevumScanner
import src.aevumParser.AevumParser2
import src.aevumEvaluator.AevumEvaluator2
import src.error.ParserErrorHandler
import java.io.File
import java.nio.charset.Charset
import java.util.Scanner as InputScanner
import kotlin.system.exitProcess

object MainLab4 {
    private val interpreter = AevumEvaluator2()

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size > 1) {
            println("Usage: aevum [script]")
            exitProcess(64)
        } else if (args.size == 1) {
            runFile(args[0])
        } else {
            runPrompt()
        }
    }

    // Script Mode
    private fun runFile(path: String) {
        val file = File(path)
        if (!file.exists()) {
            System.err.println("Error: File '$path' not found.")
            exitProcess(74)
        }
        val bytes = file.readBytes()
        run(String(bytes, Charset.defaultCharset()))
        // If we had error tracking flags, we would check them here to set exit code
    }

    // REPL Mode
// REPL Mode
    private fun runPrompt() {
        val input = InputScanner(System.`in`)
        println("Aevum REPL (Lab 4). Type 'exit' to quit.")

        val inputBuffer = StringBuilder()
        var braceCount = 0

        while (true) {
            // Show "> " for new commands, "  " for continuation lines
            if (braceCount == 0) {
                print("> ")
            } else {
                print("  ")
            }

            if (!input.hasNextLine()) break
            val line = input.nextLine()

            if (line == null || (line.equals("exit", ignoreCase = true) && braceCount == 0)) break
            if (line.isBlank() && braceCount == 0) continue

            inputBuffer.append(line).append("\n")

            // Count braces to determine if the block is finished
            for (char in line) {
                if (char == '{') braceCount++
                if (char == '}') braceCount--
            }

            // If braces are balanced (or negative/error), execute the chunk
            if (braceCount <= 0) {
                run(inputBuffer.toString())
                inputBuffer.clear()
                braceCount = 0
            }
        }
    }

    private fun run(source: String) {
        // 1. Scan
        val scanner = AevumScanner()
        // Note: scanLine in your previous code took lineNumber.
        // If you still use that signature, pass 1 or track it.
        // Assuming scanLine(source, 1) or similar based on your Scanner implementation:
        val tokens = scanner.scanLine(source, 1)

        if (tokens.isNotEmpty()) {
            // 2. Parse (Using AevumParser2 which returns List<Stmt>)
            val parser = AevumParser2(tokens)

            try {
                val statements = parser.parse()

                // 3. Interpret Statements
                interpreter.interpret(statements)

            } catch (e: ParserErrorHandler.ParseError) {
                // ParserErrorHandler prints the message, we just catch to reset
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}