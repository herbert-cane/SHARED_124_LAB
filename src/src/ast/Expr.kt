package src.ast

import src.token.Token

/**
 * Abstract Syntax Tree (AST) node hierarchy for the Aevum language.
 *
 * The AST represents the syntactic structure of source code in a tree format
 * where each node corresponds to a language construct. This sealed class hierarchy
 * defines all possible expression types that can appear in Aevum programs.
 *
 * The AST is built by the parser and serves as input to later compilation phases
 * like interpretation, compilation, or static analysis.
 */

sealed class Expr {

    /**
     * Represents binary operations with two operands and an operator.
     *
     * Examples:
     * - Arithmetic: 2 + 3, 5 * 4
     * - Comparison: x > y, a == b
     * - Logical: true && false
     *
     * @property left The left-hand side expression
     * @property operator The binary operator token (e.g., +, -, *, /, ==, !=)
     * @property right The right-hand side expression
     */
    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()

    /**
     * Represents a parenthesized expression for explicit grouping.
     *
     * Used to override operator precedence or clarify expression structure.
     * Example: (2 + 3) * 4 - the parentheses force addition before multiplication.
     *
     * @property expression The expression contained within the parentheses
     */
    data class Grouping(val expression: Expr) : Expr()

    /**
     * Represents literal values - the basic constants of the language.
     *
     * Supported literal types:
     * - Numbers: 42, 3.14
     * - Strings: "hello", "world"
     * - Booleans: true, false
     * - Null: nil
     *
     * @property value The literal value, or null for the nil literal
     */
    data class Literal(val value: Any?) : Expr()

    /**
     * Represents unary operations with one operand and an operator.
     *
     * Examples:
     * - Logical negation: !true
     * - Arithmetic negation: -5
     *
     * @property operator The unary operator token (e.g., !, -)
     * @property right The operand expression
     */
    data class Unary(val operator: Token, val right: Expr) : Expr()

    /**
     * Represents variable references by name.
     *
     * Example: In the expression "x + 1", "x" is a variable reference.
     * The actual value is resolved during evaluation by looking up the variable
     * in the current environment.
     *
     * @property name The identifier token containing the variable name
     */
    data class Variable(val name: Token) : Expr()

    // === [Lab 4]: Variable Assignment class ===
    data class Assign(val name: Token, val value: Expr) : Expr()


    // === [Lab 5]: Logical expression & Function classes ===

    // Logical expression for AND, OR
    data class Logical(val left: Expr, val operator: Token, val right: Expr) : Expr()

    // Function
    data class FunctionCall(val callee: Expr, val paren: Token, val arguments: Expr?) : Expr()
}