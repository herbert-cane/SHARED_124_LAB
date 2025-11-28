package src.ast

/**
 * AST Printer - Converts Abstract Syntax Tree nodes into human-readable string representations.
 *
 * This class implements the Visitor pattern to traverse the AST and generate
 * a parenthesized LISP-like notation that clearly shows the tree structure.
 */
class AstPrinter {

    /**
     * Converts an AST expression to its string representation.
     *
     * @param expr The expression to print, can be null for error cases
     * @return String representation of the expression in parenthesized notation
     */
    fun print(expr: Expr?): String {
        return if (expr != null) {
            // Use pattern matching to handle different expression types
            when (expr) {
                // Binary expressions: (operator left right)
                // Example: (+ 1 2) for "1 + 2"
                is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)

                // Grouping expressions: (group expression)
                // Example: (group (+ 1 2)) for "(1 + 2)"
                is Expr.Grouping -> parenthesize("group", expr.expression)

                // Literal values: output the value directly
                // Examples: "42.0", "hello", "true", "nil"
                is Expr.Literal -> expr.value?.toString() ?: "nil"

                // Unary expressions: (operator right)
                // Example: (- 5) for "-5" or (! true) for "!true"
                is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)

                // Variable references: output the variable name
                // Example: "x" for variable x
                is Expr.Variable -> expr.name.lexeme
                is Expr.Assign -> TODO()
            }
        } else {
            // Handle null expressions (typically from parsing errors)
            "null"
        }
    }

    /**
     * Helper method to create parenthesized expressions.
     *
     * Formats expressions in LISP-style: (name child1 child2 ...)
     * This makes the AST structure explicit and easy to read.
     *
     * @param name The operator or expression type name
     * @param expr Child expressions to be nested inside the parentheses
     * @return Parenthesized string representation
     */
    private fun parenthesize(name: String, vararg expr: Expr): String {
        val builder = StringBuilder()

        // Start with opening parenthesis and operator name
        builder.append("(").append(name)

        // Recursively print each child expression, separated by spaces
        for (expr in expr) {
            builder.append(" ").append(print(expr))
        }

        // Close the parentheses
        builder.append(")")

        return builder.toString()
    }
}