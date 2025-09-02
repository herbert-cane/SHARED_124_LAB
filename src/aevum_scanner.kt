import java.util.Scanner as InputScanner

enum class TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // Special
    EOF, COMMENT,

    // Error tokens
    UNTERMINATED_STRING, INVALID_NUMBER
}

fun tokenTypeToString(type: TokenType): String {
    return when (type) {
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
        TokenType.BANG -> "BANG"
        TokenType.BANG_EQUAL -> "BANG_EQUAL"
        TokenType.EQUAL -> "EQUAL"
        TokenType.EQUAL_EQUAL -> "EQUAL_EQUAL"
        TokenType.GREATER -> "GREATER"
        TokenType.GREATER_EQUAL -> "GREATER_EQUAL"
        TokenType.LESS -> "LESS"
        TokenType.LESS_EQUAL -> "LESS_EQUAL"
        TokenType.IDENTIFIER -> "IDENTIFIER"
        TokenType.STRING -> "STRING"
        TokenType.NUMBER -> "NUMBER"
        TokenType.AND -> "AND"
        TokenType.CLASS -> "CLASS"
        TokenType.ELSE -> "ELSE"
        TokenType.FALSE -> "FALSE"
        TokenType.FUN -> "FUN"
        TokenType.FOR -> "FOR"
        TokenType.IF -> "IF"
        TokenType.NIL -> "NIL"
        TokenType.OR -> "OR"
        TokenType.PRINT -> "PRINT"
        TokenType.RETURN -> "RETURN"
        TokenType.SUPER -> "SUPER"
        TokenType.THIS -> "THIS"
        TokenType.TRUE -> "TRUE"
        TokenType.VAR -> "VAR"
        TokenType.WHILE -> "WHILE"
        TokenType.EOF -> "EOF"
        TokenType.COMMENT -> "COMMENT"
        TokenType.UNTERMINATED_STRING -> "UNTERMINATED_STRING"
        TokenType.INVALID_NUMBER -> "INVALID_NUMBER"
    }
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any? = null,
    val line: Int
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
        "and" to TokenType.AND,
        "class" to TokenType.CLASS,
        "else" to TokenType.ELSE,
        "false" to TokenType.FALSE,
        "fun" to TokenType.FUN,
        "for" to TokenType.FOR,
        "if" to TokenType.IF,
        "nil" to TokenType.NIL,
        "or" to TokenType.OR,
        "print" to TokenType.PRINT,
        "return" to TokenType.RETURN,
        "super" to TokenType.SUPER,
        "this" to TokenType.THIS,
        "true" to TokenType.TRUE,
        "var" to TokenType.VAR,
        "while" to TokenType.WHILE
    )

    private fun emitToken(type: TokenType, lexeme: String, literal: Any? = null, line: Int) {
        val token = Token(type, lexeme, literal, line)
        println(token)
    }

    private fun isValidNumber(numberStr: String): Boolean {
        if (numberStr.count { it == '.' } > 1) {
            return false // Multiple decimal points
        }

        // Check if it starts or ends with a decimal point without digits
        if (numberStr.startsWith('.') && numberStr.length == 1) {
            return false
        }

        if (numberStr.endsWith('.') && numberStr.length > 1) {
            // Check if there's at least one digit before the decimal point
            val beforeDecimal = numberStr.dropLast(1)
            if (!beforeDecimal.all { it.isDigit() }) {
                return false
            }
        }

        // Try to parse as double to validate
        return numberStr.toDoubleOrNull() != null
    }

    fun scanLine(lineText: String, lineNumber: Int) {
        var current = 0
        val length = lineText.length

        while (current < length) {
            val start = current
            val char = lineText[current]

            // Skip whitespace
            if (char.isWhitespace()) {
                current++
                continue
            }

            // Single character tokens
            when (char) {
                '(' -> { emitToken(TokenType.LEFT_PAREN, "(", null, lineNumber); current++; continue }
                ')' -> { emitToken(TokenType.RIGHT_PAREN, ")", null, lineNumber); current++; continue }
                '{' -> { emitToken(TokenType.LEFT_BRACE, "{", null, lineNumber); current++; continue }
                '}' -> { emitToken(TokenType.RIGHT_BRACE, "}", null, lineNumber); current++; continue }
                ',' -> { emitToken(TokenType.COMMA, ",", null, lineNumber); current++; continue }
                '.' -> { emitToken(TokenType.DOT, ".", null, lineNumber); current++; continue }
                '-' -> { emitToken(TokenType.MINUS, "-", null, lineNumber); current++; continue }
                '+' -> { emitToken(TokenType.PLUS, "+", null, lineNumber); current++; continue }
                ';' -> { emitToken(TokenType.SEMICOLON, ";", null, lineNumber); current++; continue }
                '*' -> { emitToken(TokenType.STAR, "*", null, lineNumber); current++; continue }
            }

            // One or two character tokens
            if (char == '!') {
                if (current + 1 < length && lineText[current + 1] == '=') {
                    emitToken(TokenType.BANG_EQUAL, "!=", null, lineNumber)
                    current += 2
                } else {
                    emitToken(TokenType.BANG, "!", null, lineNumber)
                    current++
                }
                continue
            }

            if (char == '=') {
                if (current + 1 < length && lineText[current + 1] == '=') {
                    emitToken(TokenType.EQUAL_EQUAL, "==", null, lineNumber)
                    current += 2
                } else {
                    emitToken(TokenType.EQUAL, "=", null, lineNumber)
                    current++
                }
                continue
            }

            if (char == '<') {
                if (current + 1 < length && lineText[current + 1] == '=') {
                    emitToken(TokenType.LESS_EQUAL, "<=", null, lineNumber)
                    current += 2
                } else {
                    emitToken(TokenType.LESS, "<", null, lineNumber)
                    current++
                }
                continue
            }

            if (char == '>') {
                if (current + 1 < length && lineText[current + 1] == '=') {
                    emitToken(TokenType.GREATER_EQUAL, ">=", null, lineNumber)
                    current += 2
                } else {
                    emitToken(TokenType.GREATER, ">", null, lineNumber)
                    current++
                }
                continue
            }

            // Comments
            if (char == '/') {
                if (current + 1 < length) {
                    when (lineText[current + 1]) {
                        '/' -> {
                            // Single line comment - skip rest of line
                            current = length
                            continue
                        }
                        '*' -> {
                            // Multi-line comment start
                            current += 2
                            while (current < length - 1) {
                                if (lineText[current] == '*' && lineText[current + 1] == '/') {
                                    current += 2
                                    break
                                }
                                current++
                            }
                            continue
                        }
                        else -> {
                            emitToken(TokenType.SLASH, "/", null, lineNumber)
                            current++
                            continue
                        }
                    }
                } else {
                    emitToken(TokenType.SLASH, "/", null, lineNumber)
                    current++
                    continue
                }
            }

            // String literals
            if (char == '"') {
                current++
                val stringStart = current

                // Find the closing quote
                while (current < length && lineText[current] != '"') {
                    current++
                }

                if (current >= length) {
                    // Unterminated string - no closing quote found
                    val content = lineText.substring(stringStart)
                    emitToken(TokenType.UNTERMINATED_STRING, "\"$content", content, lineNumber)
                } else {
                    // Properly terminated string
                    val content = lineText.substring(stringStart, current)
                    emitToken(TokenType.STRING, "\"$content\"", content, lineNumber)
                    current++ // Skip closing quote
                }
                continue
            }

            // Numbers
            if (char.isDigit()) {
                val numberStart = current

                // Consume digits and at most one decimal point
                var decimalPointFound = false
                while (current < length) {
                    val currentChar = lineText[current]
                    if (currentChar.isDigit()) {
                        current++
                    } else if (currentChar == '.' && !decimalPointFound && current + 1 < length && lineText[current + 1].isDigit()) {
                        decimalPointFound = true
                        current++
                    } else {
                        break
                    }
                }

                val numberStr = lineText.substring(numberStart, current)

                if (isValidNumber(numberStr)) {
                    val numberValue = numberStr.toDouble()
                    emitToken(TokenType.NUMBER, numberStr, numberValue, lineNumber)
                } else {
                    emitToken(TokenType.INVALID_NUMBER, numberStr, numberStr, lineNumber)
                }
                continue
            }

            // Identifiers and keywords
            if (char.isLetter() || char == '_') {
                while (current < length && (lineText[current].isLetterOrDigit() || lineText[current] == '_')) {
                    current++
                }
                val text = lineText.substring(start, current)
                val type = keywords[text] ?: TokenType.IDENTIFIER
                emitToken(type, text, null, lineNumber)
                continue
            }

            // Unknown character (treat as identifier for now)
            emitToken(TokenType.IDENTIFIER, char.toString(), null, lineNumber)
            current++
        }

        // End of line
        emitToken(TokenType.EOF, "", null, lineNumber)
    }
}

fun main() {
    val inputScanner = InputScanner(System.`in`)
    var lineNumber = 1

    println("Welcome to Aevum REPL. Type your code below:")
    println("Type 'exit' to quit.")

    while (true) {
        print("> ")
        val line = inputScanner.nextLine().trim()
        if (line.equals("exit", ignoreCase = true)) {
            break
        }
        AevumScanner.scanLine(line, lineNumber)
        lineNumber++
    }

    println("Goodbye!")
}