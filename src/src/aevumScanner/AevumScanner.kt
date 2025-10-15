package src.aevumScanner

import src.tokenType.TokenType
import src.token.Token
import src.tokenType.keywords.Keywords
import src.singleCharTokens.SingleCharTokens
import src.doubleCharTokens.DoubleCharToken

class AevumScanner {
    val singleChar = SingleCharTokens()
    val doubleChar = DoubleCharToken()
    val keywords = Keywords()
    var current = 0

    // Two character operators (e.g., `==`, `<=`)

    // Function to emit tokens (output token details)
    private fun emitToken(type: TokenType, lexeme: String, literal: Any? = null, line: Int) {
        println(Token(type, lexeme, literal, line))
    }

    // Function to emit error messages
    private fun emitError(message: String, line: Int) {
        println("ERROR at line $line: $message")
    }

    // Refactored scanLine function
    fun scanLine(lineText: String, lineNumber: Int) {
        val length = lineText.length

        while (current < length) {
            val char = lineText[current]

            when {
                // Handle whitespace
                char.isWhitespace() -> current++

                // Handle single-character tokens
                char in singleChar.singleCharTokens -> handlesingleCharToken(char, lineText, lineNumber)

                // Handle two-character operators (like ==, <=, etc.)
                char in doubleChar.twoCharOperators -> handleTwoCharOperator(char, lineText, lineNumber)

                // Handle comments (both // and /* */)
                char == '/' -> handleComment(lineText, lineNumber)

                // Handle string literals
                char == '"' -> handleStringLiteral(lineText, lineNumber)

                // Handle numbers (integers and decimals)
                char.isDigit() || (char == '.' && current + 1 < length && lineText[current + 1].isDigit()) ->
                    handleNumber(lineText, lineNumber)

                // Handle identifiers and keywords
                char.isLetter() || char == '_' -> handleIdentifierOrKeyword(lineText, lineNumber)

                // Unknown characters (invalid lexemes)
                else -> {
                    emitError("Invalid character '$char'", lineNumber)
                    current++
                }
            }
        }

        // End of file (EOF) token
        emitToken(TokenType.EOF, "", null, lineNumber)
    }

    // Handle single-character tokens
    private fun handlesingleCharToken(char: Char, lineText: String, lineNumber: Int) {
        emitToken(singleChar.singleCharTokens[char]!!, char.toString(), null, lineNumber)
        current++
    }

    // Handle two-character operators
    private fun handleTwoCharOperator(char: Char, lineText: String, lineNumber: Int) {
        val expectedNext = doubleChar.twoCharOperators[char] ?: return
        if (current + 1 < lineText.length && lineText[current + 1] == expectedNext[0]) {
            emitToken(when (char) {
                '!' -> TokenType.NOT_EQUAL
                '=' -> TokenType.EQUAL_EQUAL
                '<' -> TokenType.LESS_EQUAL
                '>' -> TokenType.GREATER_EQUAL
                else -> throw IllegalStateException("Unexpected operator: $char")
            }, "$char$expectedNext", null, lineNumber)
            current += 2
        } else if (char == '!') {
            emitToken(TokenType.NOT, "!", null, lineNumber)
            current++
        }
    }

    // Handle comments (both single-line // and multi-line /* */)
    private fun handleComment(lineText: String, lineNumber: Int) {
        if (current + 1 < lineText.length && lineText[current + 1] == '/') {
            // Single-line comment: Skip till end of line
            current = lineText.length
        } else if (current + 1 < lineText.length && lineText[current + 1] == '*') {
            // Multi-line comment: Skip until closing */
            current += 2
            while (current < lineText.length - 1 && !(lineText[current] == '*' && lineText[current + 1] == '/')) {
                current++
            }
            if (current < lineText.length - 1) current += 2
        }
    }

    // Handle string literals
    private fun handleStringLiteral(lineText: String, lineNumber: Int) {
        current++
        val start = current
        while (current < lineText.length && lineText[current] != '"') current++

        if (current >= lineText.length) {
            emitError("Unterminated string literal", lineNumber)
        } else {
            val content = lineText.substring(start, current)
            emitToken(TokenType.STRING, "\"$content\"", content, lineNumber)
            current++ // Skip closing quote
        }
    }

    // Handle numbers (integers and decimals)
    private fun handleNumber(lineText: String, lineNumber: Int) {
        val start = current
        while (current < lineText.length && (lineText[current].isDigit() || lineText[current] == '.')) current++
        val numberStr = lineText.substring(start, current)
        if (numberStr.count { it == '.' } > 1) {
            emitError("Invalid number format: $numberStr", lineNumber)
        } else {
            emitToken(TokenType.NUMBER, numberStr, numberStr.toDouble(), lineNumber)
        }
    }

    // Handle identifiers and keywords
    private fun handleIdentifierOrKeyword(lineText: String, lineNumber: Int) {
        val start = current
        while (current < lineText.length && (lineText[current].isLetterOrDigit() || lineText[current] == '_')) current++

        val text = lineText.substring(start, current)
        val type = keywords.keywords[text] ?: TokenType.IDENTIFIER
        emitToken(TokenType.IDENTIFIER, text, null, lineNumber)
    }
}