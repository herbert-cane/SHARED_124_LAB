package src.aevumFunctions

import src.aevumEvaluator.AevumEvaluator2
import src.ast.Stmt
import src.ast.Expr
import src.aevumEnvironment.Environment

// === [Lab 5] User-Defined Functions ===

/**
 * Represents a function defined in Aevum code.
 *
 * It implements the [AevumCallable] interface so it can be invoked.
 * Crucially, it holds a [closure], which captures the environment
 * where the function was *declared* (Lexical Scoping).
 */
class AevumFunction(
    private val declaration: Stmt.Function, // The AST node (name, params, body)
    // === [Lab 5] FI: Closures (capturing enclosing environment) ===
    private val closure: Environment        // The environment snapshot (Closure)
) : AevumCallable {

    // === Arity Checking ===
    override fun arity(): Int {
        // Since we store parameters as a Tree (Expr) rather than a List,
        // we must traverse the tree to count them.
        return countParameters(declaration.parameters)
    }

    private fun countParameters(expr: Expr?): Int {
        if (expr == null) return 0
        // If it's a binary tree (comma separated), count 1 + rest
        if (expr is Expr.Binary) {
            return countParameters(expr.left) + countParameters(expr.right)
        }
        // If it's a single variable leaf, count 1
        return 1
    }

    // === Execution Logic ===
    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val environment = Environment(closure)

        // Use Pair-based binding logic
        bindParameters(declaration.parameters, arguments, environment)

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }

        return null
    }

    // Recursive helper to map a flat list of arguments to a tree of parameter names.
    private fun bindParameters(parameterExpr: Expr?, argValue: Any?, env: Environment) {
        if (parameterExpr == null) return

        if (parameterExpr is Expr.Binary) {
            // Case: Multiple parameters (linked by commas)
            // Left child is the current parameter name
            // 'argValue' is expected to be a Pair(CurrentVal, RestVals)
            val pair = argValue as? Pair<*, *>

            // Bind the current parameter (Left) to the first value
            bindParameters(parameterExpr.left, pair?.first, env)

            // Recursively bind the rest of the tree (Right) to the rest of the values
            bindParameters(parameterExpr.right, pair?.second, env)

        } else if (parameterExpr is Expr.Variable) {
            // Case: Single leaf parameter
            env.define(parameterExpr.name.lexeme, argValue)
        }
    }

    /**
     * A helper function that converts a List into a nested Pair chain.
     * This is NOT needed anymore, but we keep it for future use
     * in case 'call' gives us a List, but 'bindParameters' wants Pairs.
     */
    private fun listToPair(args: List<Any?>, index: Int): Any? {
        if (index >= args.size) return null

        // If it's the last element, just return the value (Base Case)
        if (index == args.size - 1) return args[index]

        // Recursive Step: Create a Pair(Current, Rest)
        return Pair(args[index], listToPair(args, index + 1))
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}