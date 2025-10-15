import java.util.Scanner as InputScanner
import src.tokenType.TokenType
import src.aevumScanner.AevumScanner
import src.token.Token


fun main() {
    val inputScanner = InputScanner(System.`in`)
    var lineNumber = 1
    val scanner = AevumScanner()

    println("Welcome to Aevum REPL. Type your code below:")
    println("Type 'exit' to quit.")

    while (true) {
        print("> ")
        val line = inputScanner.nextLine().trim()
        if (line.equals("exit", ignoreCase = true)) break
        scanner.scanLine(line, lineNumber++)
    }


    println("Goodbye!")
}