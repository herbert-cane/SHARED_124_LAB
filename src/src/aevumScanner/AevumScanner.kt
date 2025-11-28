package src.aevumScanner

import src.tokenType.TokenType
import src.token.Token
import src.singleCharTokens.SingleCharTokens

class AevumScanner {
    val singleChar = SingleCharTokens()
    var current = 0
    var line = 1 // Track line internally

    private fun emitToken(type: TokenType, lexeme: String, literal: Any? = null, line: Int): Token {
        return Token(type, lexeme, literal, line)
    }

    private fun emitError(message: String, line: Int) {
        println("ERROR at line $line: $message")
    }

    // Renamed to 'scan' since it handles full source, not just one line
    fun scanLine(source: String, startLine: Int = 1): List<Token> {
        current = 0
        line = startLine // Initialize line number
        val length = source.length
        val tokens = mutableListOf<Token>()

        while (current < length) {
            val char = source[current]

            when {
                // [FIX] Handle newlines explicitly to increment line counter
                char == '\n' -> {
                    line++
                    current++
                }
                char.isWhitespace() -> {
                    current++
                }

                // Handle comments first
                char == '/' -> {
                    if (handleComment(source)) {
                        // Comment was handled
                        continue
                    } else {
                        // It's a division operator
                        tokens.add(handleSingleCharToken(char, line))
                    }
                }

                // Handle two-character operators
                isTwoCharOperatorStart(char, source) -> {
                    handleTwoCharOperator(source, line)?.let { tokens.add(it) }
                }

                // Handle single-character tokens
                char in singleChar.singleCharTokens -> {
                    tokens.add(handleSingleCharToken(char, line))
                }

                // Handle string literals
                char == '"' -> {
                    handleStringLiteral(source, line)?.let { tokens.add(it) }
                }

                // Handle numbers
                char.isDigit() -> {
                    handleNumber(source, line)?.let { tokens.add(it) }
                }

                // Handle identifiers and keywords
                char.isLetter() || char == '_' -> {
                    tokens.add(handleIdentifierOrKeyword(source, line))
                }

                else -> {
                    emitError("Invalid character '$char'", line)
                    current++
                }
            }
        }

        tokens.add(emitToken(TokenType.EOF, "", null, line))

        // Remove redundant tokens filter if not needed,
        // but keeping your logic to filter EOF/blanks if you want:
        return tokens.filter {
            // Keep EOF to signal end of parsing
            it.type == TokenType.EOF || !it.lexeme.isBlank()
        }
    }

    // ... (isTwoCharOperatorStart, handleTwoCharOperator, handleSingleCharToken remain the same) ...
    // Make sure to pass 'line' instead of 'lineNumber' to them.

    private fun isTwoCharOperatorStart(char: Char, lineText: String): Boolean {
        if (current + 1 >= lineText.length) return false
        return when (char) {
            '!', '=', '<', '>' -> true
            else -> false
        }
    }

    private fun handleTwoCharOperator(lineText: String, lineNumber: Int): Token? {
        // (Your existing logic is fine here)
        val char = lineText[current]
        val nextChar = if (current + 1 < lineText.length) lineText[current + 1] else null

        return when {
            char == '!' && nextChar == '=' -> {
                val token = emitToken(TokenType.NOT_EQUAL, "!=", null, lineNumber)
                current += 2
                token
            }
            char == '=' && nextChar == '=' -> {
                val token = emitToken(TokenType.EQUAL_EQUAL, "==", null, lineNumber)
                current += 2
                token
            }
            char == '<' && nextChar == '=' -> {
                val token = emitToken(TokenType.LESS_EQUAL, "<=", null, lineNumber)
                current += 2
                token
            }
            char == '>' && nextChar == '=' -> {
                val token = emitToken(TokenType.GREATER_EQUAL, ">=", null, lineNumber)
                current += 2
                token
            }
            char == '!' -> {
                val token = emitToken(TokenType.NOT, "!", null, lineNumber)
                current++
                token
            }
            char == '=' -> {
                val token = emitToken(TokenType.EQUAL, "=", null, lineNumber)
                current++
                token
            }
            char == '<' -> {
                val token = emitToken(TokenType.LESS, "<", null, lineNumber)
                current++
                token
            }
            char == '>' -> {
                val token = emitToken(TokenType.GREATER, ">", null, lineNumber)
                current++
                token
            }
            else -> null
        }
    }

    private fun handleSingleCharToken(char: Char, lineNumber: Int): Token {
        val token = emitToken(singleChar.singleCharTokens[char]!!, char.toString(), null, lineNumber)
        current++
        return token
    }

    // [FIX] Correct Comment Handling for multi-line source
    private fun handleComment(source: String): Boolean {
        if (current + 1 < source.length && source[current + 1] == '/') {
            // Single-line comment: Skip until NEWLINE or EOF
            current += 2
            while (current < source.length && source[current] != '\n') {
                current++
            }
            // We do NOT consume the \n here.
            // The main loop will see it next, increment line++, and consume it.
            return true
        } else if (current + 1 < source.length && source[current + 1] == '*') {
            // Multi-line comment: Skip until closing */
            current += 2
            while (current < source.length - 1) {
                if (source[current] == '\n') {
                    line++ // Increment line count inside multi-line comments
                }
                if (source[current] == '*' && source[current + 1] == '/') {
                    current += 2 // Consume */
                    return true
                }
                current++
            }
            // If we hit EOF without closing */
            emitError("Unterminated comment", line)
            return true // Treat as handled to avoid infinite loops
        }
        return false
    }

    // ... (The rest: handleStringLiteral, handleNumber, handleIdentifierOrKeyword remain the same) ...
    // Just ensure they use the correct 'current' index.

    private fun handleStringLiteral(lineText: String, lineNumber: Int): Token? {
        current++
        val start = current
        while (current < lineText.length && lineText[current] != '"') {
            if (lineText[current] == '\n') line++ // [FIX] Track newlines in strings
            current++
        }
        if (current >= lineText.length) {
            emitError("Unterminated string literal", lineNumber)
            return null
        } else {
            val content = lineText.substring(start, current)
            val token = emitToken(TokenType.STRING, "\"$content\"", content, lineNumber)
            current++
            return token
        }
    }

    private fun handleNumber(lineText: String, lineNumber: Int): Token? {
        val start = current
        while (current < lineText.length && lineText[current].isDigit()) {
            current++
        }
        if (current < lineText.length && lineText[current] == '.' &&
            current + 1 < lineText.length && lineText[current + 1].isDigit()) {
            current++
            while (current < lineText.length && lineText[current].isDigit()) {
                current++
            }
        }
        val numberStr = lineText.substring(start, current)
        return try {
            val value = numberStr.toDouble()
            emitToken(TokenType.NUMBER, numberStr, value, lineNumber)
        } catch (_: NumberFormatException) {
            emitError("Invalid number format: $numberStr", lineNumber)
            null
        }
    }

    private fun handleIdentifierOrKeyword(lineText: String, lineNumber: Int): Token {
        val start = current
        while (current < lineText.length && (lineText[current].isLetterOrDigit() || lineText[current] == '_')) {
            current++
        }
        val text = lineText.substring(start, current)
        val type = getKeywordType(text) ?: TokenType.IDENTIFIER
        return emitToken(type, text, null, lineNumber)
    }

    private fun getKeywordType(text: String): TokenType? {
        return when (text) {
            "true" -> TokenType.TRUE
            "false" -> TokenType.FALSE
            "nil" -> TokenType.NIL
            "and" -> TokenType.AND
            "or" -> TokenType.OR
            "if" -> TokenType.IF
            "else" -> TokenType.ELSE
            "for" -> TokenType.FOR
            "while" -> TokenType.WHILE
            "fun" -> TokenType.FUN
            "return" -> TokenType.RETURN
            "var" -> TokenType.VAR
            "class" -> TokenType.CLASS
            "super" -> TokenType.SUPER
            "print" -> TokenType.PRINT
            else -> null
        }
    }
}