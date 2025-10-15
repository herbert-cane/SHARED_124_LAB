import src.aevumScanner.AevumScanner
import java.util.Scanner as InputScanner

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

        // Pass the current line to the scanner for processing
        scanner.scanLine(line, lineNumber)
        lineNumber++  // Increment line number after processing
    }

    println("Goodbye!")
}
