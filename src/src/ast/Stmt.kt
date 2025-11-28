// File: src/ast/Stmt.kt
package src.ast

import src.token.Token

sealed class Stmt {
    // Example: "print 1 + 1;"
    data class Print(val expression: Expr) : Stmt()

    // Example: "1 + 1;" (Evaluating for side effects)
    data class Expression(val expression: Expr) : Stmt()

    // Example: "var x = 10;"
    data class Var(val name: Token, val initializer: Expr?) : Stmt()

    // Example: "{ var x = 1; print x; }"
    data class Block(val statements: List<Stmt>) : Stmt()
}