package src.aevumFunctions
import src.aevumEvaluator.AevumEvaluator2

// === [Lab 5] Function Abstraction ===

/**
 * The interface for any object that can be called like a function.
 *
 * This allows us to treat user-defined functions (AevumFunction) and
 * native built-in functions (like clock()) uniformly.
 */


interface AevumCallable {

    /**
     * Returns the number of arguments this function expects (arity).
     * Used for runtime error checking before execution.
     */
    fun arity() : Int

    /**
     * Executes the function logic.
     *
     * @param interpreter The current evaluator instance (needed to execute function bodies).
     * @param arguments The evaluated values passed to the function.
     * @return The result of the function call.
     */
    fun call(interpreter:AevumEvaluator2, arguments: Any?) : Any?
}
