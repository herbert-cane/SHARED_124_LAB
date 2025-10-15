package src.tokenType.keywords

import src.tokenType.TokenType

class Keywords {
// Keywords and their respective token types
public val keywords = mapOf(
    "start" to TokenType.START, "speak" to TokenType.SPEAK, "choice" to TokenType.CHOICE,
    "option" to TokenType.OPTION, "action" to TokenType.ACTION, "endgame" to TokenType.ENDGAME,
    "continue" to TokenType.CONTINUE, "if" to TokenType.IF, "else" to TokenType.ELSE,
    "var" to TokenType.VAR, "print" to TokenType.PRINT, "restart" to TokenType.RESTART, "inventory" to TokenType.INVENTORY
)

}