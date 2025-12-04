package src.aevumEvaluator

import src.aevumFunctions.AevumCallable

// 1 Argument: arguments is just the value
class NativeSpeak : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val message = EvaluatorUtils.stringify(arguments)
        println(message)
        return null
    }
}

// 2 Arguments: arguments is Pair(First, Second)
class NativeSay : AevumCallable {
    override fun arity(): Int = 2

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        // Unpack the tree manually
        val pair = arguments as? Pair<*, *>
        val name = EvaluatorUtils.stringify(pair?.first)
        val message = EvaluatorUtils.stringify(pair?.second)

        println("$name: \"$message\"")
        return null
    }

}

/**
 * Native implementation of 'character' (used to define a scene context).
 * Usage: character("The Dark Forest")
 * Arity: 1 -> Arguments received as the direct value
 */
class NativeCharacter : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val sceneName = EvaluatorUtils.stringify(arguments)
        println("\n=== SCENE: $sceneName ===")
        return null
    }

    override fun toString() = "<native fn character>"
}