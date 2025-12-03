package src.ast

import src.token.Token

sealed class Stmt {
    // The Root Node. It holds the top of the tree.
    data class Program(val body: Stmt?) : Stmt()

    // 'first' is the left child (current statement)
    // 'next' is the right child (the rest of the code)
    data class Sequence(val first: Stmt, val next: Stmt) : Stmt()

    /** Now, nodes look like this:
     *  [Sequence]
     *  [Stmt A]    [Sequence]
     *              [Stmt B][Stmt B]
     */
    // Block holds a single Stmt (which might be a Sequence tree)
    data class Block(val body: Stmt?) : Stmt()

    data class Print(val expression: Expr) : Stmt()
    data class Expression(val expression: Expr) : Stmt()
    data class Var(val name: Token, val initializer: Expr?) : Stmt()

    // [Lab 5]
    // Control Flow
    data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?) : Stmt()
    data class While(val condition: Expr, val body: Stmt) : Stmt()
    data class For(val initializer: Stmt?, val condition: Expr?, val updateExpression: Expr?, val body: Stmt) : Stmt()

    // Functions
    data class Function(val name: Token, val parameters: Expr?, val body: Stmt) : Stmt()
    data class Return(val keyword: Token, val value: Expr?) : Stmt()
}