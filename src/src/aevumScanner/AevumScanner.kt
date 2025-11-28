package src.aevumScanner

import src.tokenType.TokenType
import src.token.Token
import src.singleCharTokens.SingleCharTokens
//import src.tokenType.keywords.Keywords
//import src.doubleCharTokens.DoubleCharToken

class AevumScanner {
    val singleChar = SingleCharTokens()
    //val doubleChar = DoubleCharToken()
    //val keywords = Keywords()
    var current = 0

    private fun emitToken(type: TokenType, lexeme: String, literal: Any? = null, line: Int): Token {
        val token = Token(type, lexeme, literal, line)
        return token
    }

    private fun emitError(message: String, line: Int) {
        println("ERROR at line $line: $message")
    }

    fun scanLine(lineText: String, lineNumber: Int): List<Token> {
        current = 0
        val length = lineText.length
        val tokens = mutableListOf<Token>()

        while (current < length) {
            val char = lineText[current]

            when {
                char.isWhitespace() -> {
                    current++
                }

                // Handle comments first
                char == '/' -> {
                    if (handleComment(lineText)) {
                        // Comment was handled and skipped, continue to next iteration
                        continue
                    } else {
                        // It's a division operator
                        tokens.add(handleSingleCharToken(char, lineNumber))
                    }
                }

                // Handle two-character operators
                isTwoCharOperatorStart(char, lineText) -> {
                    handleTwoCharOperator(lineText, lineNumber)?.let { tokens.add(it) }
                }

                // Handle single-character tokens
                char in singleChar.singleCharTokens -> {
                    tokens.add(handleSingleCharToken(char, lineNumber))
                }

                // Handle string literals
                char == '"' -> {
                    handleStringLiteral(lineText, lineNumber)?.let { tokens.add(it) }
                }

                // Handle numbers
                char.isDigit() -> {
                    handleNumber(lineText, lineNumber)?.let { tokens.add(it) }
                }

                // Handle identifiers and keywords
                char.isLetter() || char == '_' -> {
                    tokens.add(handleIdentifierOrKeyword(lineText, lineNumber))
                }

                else -> {
                    emitError("Invalid character '$char'", lineNumber)
                    current++
                }
            }
        }

        tokens.add(emitToken(TokenType.EOF, "", null, lineNumber))
        // Remove redundant tokens woohoo
        tokens.filter{
            it.type != TokenType.EOF &&
                    !it.lexeme.isBlank()
        }
        return tokens
    }

    private fun isTwoCharOperatorStart(char: Char, lineText: String): Boolean {
        if (current + 1 >= lineText.length) return false

        return when (char) {
            '!', '=', '<', '>' -> true
            else -> false
        }
    }

    private fun handleTwoCharOperator(lineText: String, lineNumber: Int): Token? {
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
            // Handle single character versions
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

    private fun handleComment(lineText: String): Boolean {
        if (current + 1 < lineText.length && lineText[current + 1] == '/') {
            // Single-line comment: Skip till end of line
            current = lineText.length
            return true
        } else if (current + 1 < lineText.length && lineText[current + 1] == '*') {
            // Multi-line comment: Skip until closing */
            current += 2
            while (current < lineText.length - 1 && !(lineText[current] == '*' && lineText[current + 1] == '/')) {
                current++
            }
            if (current < lineText.length - 1) current += 2
            return true
        }
        return false
    }

    private fun handleStringLiteral(lineText: String, lineNumber: Int): Token? {
        current++ // Skip opening quote
        val start = current

        while (current < lineText.length && lineText[current] != '"') {
            current++
        }

        if (current >= lineText.length) {
            emitError("Unterminated string literal", lineNumber)
            return null
        } else {
            val content = lineText.substring(start, current)
            val token = emitToken(TokenType.STRING, "\"$content\"", content, lineNumber)
            current++ // Skip closing quote
            return token
        }
    }

    private fun handleNumber(lineText: String, lineNumber: Int): Token? {
        val start = current

        // Consume integer part
        while (current < lineText.length && lineText[current].isDigit()) {
            current++
        }

        // Consume fractional part if present
        if (current < lineText.length && lineText[current] == '.' &&
            current + 1 < lineText.length && lineText[current + 1].isDigit()) {
            current++ // Consume the dot
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