package src.singleCharTokens

import src.tokenType.TokenType

class SingleCharTokens {
    // Single character tokens (e.g., parentheses, operators)
    val singleCharTokens = mapOf(
        '(' to TokenType.LEFT_PAREN,
        ')' to TokenType.RIGHT_PAREN,
        '{' to TokenType.LEFT_BRACE,
        '}' to TokenType.RIGHT_BRACE,
        ',' to TokenType.COMMA,
        '.' to TokenType.DOT,
        '-' to TokenType.MINUS,
        '+' to TokenType.PLUS,
        ';' to TokenType.SEMICOLON,
        '/' to TokenType.SLASH,
        '*' to TokenType.STAR,
        '=' to TokenType.EQUAL,
        '<' to TokenType.GREATER,
        '>' to TokenType.LESS,
        '?' to TokenType.QMARK
    )
}