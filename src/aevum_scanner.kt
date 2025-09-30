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

    // Keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // Game-specific keywords
    SPRITE, SCENE, ANIM, COLLIDE, MOVE, SPAWN, JUMP, GRAVITY, PHYSICS,

    // Special & Error tokens
    EOF, COMMENT
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

    //AEVUM KEYWORDS
    TokenType.SPRITE -> "SPRITE"
    TokenType.SCENE -> "SCENE"
    TokenType.ANIM -> "ANIMATION"
    TokenType.COLLIDE -> "COLLISION"
    TokenType.MOVE -> "MOVEMENT"
    TokenType.SPAWN -> "SPAWN"
    TokenType.JUMP -> "JUMP"
    TokenType.GRAVITY -> "GRAVITY"
    TokenType.PHYSICS -> "PHYSICS"
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
        "and" to TokenType.AND, "class" to TokenType.CLASS, "else" to TokenType.ELSE,
        "false" to TokenType.FALSE, "fun" to TokenType.FUN, "for" to TokenType.FOR,
        "if" to TokenType.IF, "nil" to TokenType.NIL, "or" to TokenType.OR,
        "print" to TokenType.PRINT, "return" to TokenType.RETURN, "super" to TokenType.SUPER,
        "this" to TokenType.THIS, "true" to TokenType.TRUE, "var" to TokenType.VAR,
        "while" to TokenType.WHILE,


        // AEVUM-KEYWORDS
        "sprite" to TokenType.SPRITE, "scene" to TokenType.SCENE, "anim" to TokenType.ANIM,
        "collide" to TokenType.COLLIDE, "move" to TokenType.MOVE, "spawn" to TokenType.SPAWN,
        "jump" to TokenType.JUMP, "gravity" to TokenType.GRAVITY, "physics" to TokenType.PHYSICS
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

    private fun emitToken(type: TokenType, lexeme: String, literal: Any? = null, line: Int) =
        println(Token(type, lexeme, literal, line))

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

            // Handle remaining operators
            when (char) {
                '=' -> { emitToken(TokenType.EQUAL, "=", null, lineNumber); current++; continue }
                '<' -> { emitToken(TokenType.LESS, "<", null, lineNumber); current++; continue }
                '>' -> { emitToken(TokenType.GREATER, ">", null, lineNumber); current++; continue }
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
                    // Unterminated string - print error message
                    val content = lineText.substring(start)
                    println("ERROR: Unterminated string literal at line $lineNumber: \"$content")
                } else {
                    val content = lineText.substring(start, current)
                    emitToken(TokenType.STRING, "\"$content\"", content, lineNumber)
                    current++ // Skip closing quote
                }
                continue
            }

            // Handle numbers - scan multiple numbers separated by dots
            if (char.isDigit() || (char == '.' && current + 1 < length && lineText[current + 1].isDigit())) {
                val start = current

                // Scan the entire number sequence including multiple dots
                while (current < length) {
                    val c = lineText[current]
                    if (c.isDigit() || c == '.') {
                        current++
                    } else {
                        break
                    }
                }

                // Process the scanned sequence to extract individual numbers
                var scanPos = start
                while (scanPos < current) {
                    // Find the next valid number (digits followed by optional decimal and more digits)
                    var numberEnd = scanPos
                    var decimalFound = false

                    while (numberEnd < current) {
                        val c = lineText[numberEnd]
                        if (c.isDigit()) {
                            numberEnd++
                        } else if (c == '.' && !decimalFound && numberEnd + 1 < current && lineText[numberEnd + 1].isDigit()) {
                            decimalFound = true
                            numberEnd++
                        } else {
                            break
                        }
                    }

                    // Emit the number token if we found a valid number
                    if (numberEnd > scanPos) {
                        val numStr = lineText.substring(scanPos, numberEnd)
                        emitToken(TokenType.NUMBER, numStr, numStr.toDouble(), lineNumber)
                        scanPos = numberEnd
                    }

                    // Skip any extra dots between numbers
                    while (scanPos < current && lineText[scanPos] == '.') {
                        scanPos++
                    }
                }

                current = scanPos
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
    var lineNumber = 1

    println("Welcome to Aevum REPL. Type your code below:")
    println("Type 'exit' to quit.")

    while (true) {
        print("> ")
        val line = inputScanner.nextLine().trim()
        if (line.equals("exit", ignoreCase = true)) break
        AevumScanner.scanLine(line, lineNumber++)
    }

    println("Goodbye!")
}