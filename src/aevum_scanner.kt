import java.util.Scanner as InputScanner

enum class TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens
    NOT, NOT_EQUAL, EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords for text RPG
    START, SPEAK, CHOICE, OPTION, ACTION, ENDGAME, CONTINUE, IF, ELSE, VAR, PRINT, RESTART, INVENTORY,

    // Special token
    EOF
}

fun tokenTypeToString(type: TokenType) = when (type) {
    TokenType.LEFT_PAREN -> "LEFT_PAREN"
    TokenType.RIGHT_PAREN -> "RIGHT_PAREN"
    TokenType.LEFT_BRACE -> "LEFT_BRACE"
    TokenType.RIGHT_BRACE -> "RIGHT_BRACE"
    TokenType.COMMA -> "COMMA"
    TokenType.DOT -> "DOT"
    TokenType.MINUS -> "MINUS"
    TokenType.PLUS -> "PLUS"
    TokenType.SEMICOLON -> "SEMICOLON"
    TokenType.SLASH -> "SLASH"
    TokenType.STAR -> "STAR"
    TokenType.NOT -> "NOT"
    TokenType.NOT_EQUAL -> "NOT_EQUAL"
    TokenType.EQUAL -> "EQUAL"
    TokenType.EQUAL_EQUAL -> "EQUAL_EQUAL"
    TokenType.GREATER -> "GREATER"
    TokenType.GREATER_EQUAL -> "GREATER_EQUAL"
    TokenType.LESS -> "LESS"
    TokenType.LESS_EQUAL -> "LESS_EQUAL"
    TokenType.IDENTIFIER -> "IDENTIFIER"
    TokenType.STRING -> "STRING"
    TokenType.NUMBER -> "NUMBER"
    TokenType.START -> "START"
    TokenType.SPEAK -> "SPEAK"
    TokenType.CHOICE -> "CHOICE"
    TokenType.OPTION -> "OPTION"
    TokenType.ACTION -> "ACTION"
    TokenType.ENDGAME -> "ENDGAME"
    TokenType.CONTINUE -> "CONTINUE"
    TokenType.IF -> "IF"
    TokenType.ELSE -> "ELSE"
    TokenType.VAR -> "VAR"
    TokenType.PRINT -> "PRINT"
    TokenType.RESTART -> "RESTART"
    TokenType.INVENTORY -> "INVENTORY"
    else -> "EOF"
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any? = null,
    val line: Int,
    val description: String = ""
) {
    override fun toString(): String {
        val literalStr = when (literal) {
            null -> "null"
            is String -> "\"$literal\""
            else -> literal.toString()
        }
        return "Token(type=${tokenTypeToString(type)}, lexeme=$lexeme, literal=$literalStr, line=$line)"
    }
}

object AevumScanner {
    private val keywords = mapOf(
        "start" to TokenType.START, "speak" to TokenType.SPEAK, "choice" to TokenType.CHOICE,
        "option" to TokenType.OPTION, "action" to TokenType.ACTION, "endgame" to TokenType.ENDGAME,
        "continue" to TokenType.CONTINUE, "if" to TokenType.IF, "else" to TokenType.ELSE,
        "var" to TokenType.VAR, "print" to TokenType.PRINT, "restart" to TokenType.RESTART, "inventory" to TokenType.INVENTORY
    )

    private val singleCharTokens = mapOf(
        '(' to TokenType.LEFT_PAREN, ')' to TokenType.RIGHT_PAREN,
        '{' to TokenType.LEFT_BRACE, '}' to TokenType.RIGHT_BRACE,
        ',' to TokenType.COMMA, '.' to TokenType.DOT, '-' to TokenType.MINUS,
        '+' to TokenType.PLUS, ';' to TokenType.SEMICOLON, '*' to TokenType.STAR
    )

    private val twoCharOperators = mapOf(
        '!' to "=", '=' to "=", '<' to "=", '>' to "="
    )

    private fun emitToken(type: TokenType, lexeme: String, literal: Any? = null, line: Int) {
        println(Token(type, lexeme, literal, line))
    }

    private fun emitError(message: String, line: Int) {
        println("ERROR at line $line: $message")
    }

    fun scanLine(lineText: String, lineNumber: Int) {
        var current = 0
        val length = lineText.length

        while (current < length) {
            val char = lineText[current]

            if (char.isWhitespace()) {
                current++
                continue
            }

            // Handle single character tokens
            singleCharTokens[char]?.let {
                emitToken(it, char.toString(), null, lineNumber)
                current++
                continue
            }

            // Handle two-character operators
            twoCharOperators[char]?.let { expectedNext ->
                if (current + 1 < length && lineText[current + 1] == expectedNext[0]) {
                    emitToken(when (char) {
                        '!' -> TokenType.NOT_EQUAL
                        '=' -> TokenType.EQUAL_EQUAL
                        '<' -> TokenType.LESS_EQUAL
                        '>' -> TokenType.GREATER_EQUAL
                        else -> throw IllegalStateException("Unexpected operator: $char")
                    }, "$char$expectedNext", null, lineNumber)
                    current += 2
                    continue
                } else if (char == '!') {
                    emitToken(TokenType.NOT, "!", null, lineNumber)
                    current++
                    continue
                }
            }

            // Handle comments
            if (char == '/') {
                when {
                    current + 1 < length && lineText[current + 1] == '/' -> {
                        current = length
                        continue
                    }
                    current + 1 < length && lineText[current + 1] == '*' -> {
                        current += 2
                        while (current < length - 1 && !(lineText[current] == '*' && lineText[current + 1] == '/')) {
                            current++
                        }
                        if (current < length - 1) current += 2
                        continue
                    }
                    else -> {
                        emitToken(TokenType.SLASH, "/", null, lineNumber)
                        current++
                        continue
                    }
                }
            }

            // Handle strings - print error message for invalid strings
            if (char == '"') {
                current++
                val start = current
                while (current < length && lineText[current] != '"') current++

                if (current >= length) {
                    emitError("Unterminated string literal", lineNumber)
                } else {
                    val content = lineText.substring(start, current)
                    emitToken(TokenType.STRING, "\"$content\"", content, lineNumber)
                    current++ // Skip closing quote
                }
                continue
            }

            // Handle numbers
            if (char.isDigit() || (char == '.' && current + 1 < length && lineText[current + 1].isDigit())) {
                val start = current
                while (current < length && (lineText[current].isDigit() || lineText[current] == '.')) current++
                val numberStr = lineText.substring(start, current)
                if (numberStr.count { it == '.' } > 1) {
                    emitError("Invalid number format: $numberStr", lineNumber)
                } else {
                    emitToken(TokenType.NUMBER, numberStr, numberStr.toDouble(), lineNumber)
                }
                continue
            }

            // Handle identifiers and keywords
            if (char.isLetter() || char == '_') {
                val start = current
                while (current < length && (lineText[current].isLetterOrDigit() || lineText[current] == '_')) current++

                val text = lineText.substring(start, current)
                val type = keywords[text] ?: TokenType.IDENTIFIER
                emitToken(type, text, null, lineNumber)
                continue
            }

            // Unknown character
            emitToken(TokenType.IDENTIFIER, char.toString(), null, lineNumber)
            current++
        }

        emitToken(TokenType.EOF, "", null, lineNumber)
    }
}

fun main() {
    val inputScanner = InputScanner(System.`in`)
    val lineNumber = 1

    println("Welcome to Aevum REPL. Type your code below:")
    println("Type 'exit' to quit.")

    while (true) {
        print("> ")
        val line = inputScanner.nextLine().trim()
        if (line.equals("exit", ignoreCase = true)) break
        AevumScanner.scanLine(line, lineNumber)
    }

    println("Goodbye!")
}
