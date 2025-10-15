package src.tokenType

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
