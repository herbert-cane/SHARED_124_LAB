// File: src/environment/Environment.kt
package src.aevumEnvironment

import src.aevumEvaluator.RuntimeErrorHandler
import src.token.Token

class Environment(val enclosing: Environment? = null) {
    // Map to store variable names and their values
    private val values = HashMap<String, Any?>()

    // Define a new variable
    fun define(name: String, value: Any?) {
        values[name] = value
    }

    // Look up a variable
    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }

        // If not found here, check the parent scope
        if (enclosing != null) return enclosing.get(name)

        throw RuntimeErrorHandler.report(name, "Undefined variable '${name.lexeme}'.")
    }

    // Update an existing variable
    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }

        // If not found here, try to assign in the parent scope
        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }

        throw RuntimeErrorHandler.report(name, "Undefined variable '${name.lexeme}'.")
    }
}