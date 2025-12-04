package src.aevumFunctions

// === [Lab 5] Control Flow: Function Returns ===

/**
 * A special exception used to handle 'return' statements.
 *
 * In our interpreter, we use Java/Kotlin exceptions for control flow.
 * When a 'return' statement is executed, we throw this exception to
 * unwind the stack and jump back to the point where the function was called.
 *
 * @param value The result value being returned by the function.
 */
class Return(val value: Any?) : RuntimeException(null, null, false, false)
/** Note: We disable stack trace generation (writableStackTrace = false)
    because this is a control flow mechanism, not an actual error.
    This makes it much faster JVM-wise.                                 */