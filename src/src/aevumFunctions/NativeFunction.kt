package src.aevumFunctions

import src.aevumEvaluator.AevumEvaluator2
import src.aevumEvaluator.EvaluatorUtils

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

class NativeChoice : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val question = EvaluatorUtils.stringify(arguments)
        println("\n??? CHOICE: $question ???")
        return null
    }

    override fun toString() = "<native fn choice>"
}

/**
 * Native implementation of 'option'.
 * Usage: option("1", "Attack")
 * Arity: 2 -> Pair(Selector, Description)
 */
class NativeOption : AevumCallable {
    override fun arity(): Int = 2

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val pair = arguments as? Pair<*, *>
        val selector = EvaluatorUtils.stringify(pair?.first)
        val description = EvaluatorUtils.stringify(pair?.second)

        println("   [$selector] $description")
        return null
    }

    override fun toString() = "<native fn option>"
}

/**
 * Native implementation of 'spawn'.
 * Usage: spawn("Enemy", "Goblin")
 * Arity: 2 -> Pair(Type, Name)
 */
class NativeSpawn : AevumCallable {
    override fun arity(): Int = 2

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val pair = arguments as? Pair<*, *>
        val type = EvaluatorUtils.stringify(pair?.first)
        val name = EvaluatorUtils.stringify(pair?.second)

        println("+++ SPAWN: $name ($type) appeared! +++")
        return null
    }

    override fun toString() = "<native fn spawn>"
}

/**
 * Native implementation of 'input'.
 * Usage: var selection = input();
 * Arity: 0
 * Returns: The string typed by the user.
 */
class NativeInput : AevumCallable {
    override fun arity(): Int = 0

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any {
        print("> ") // The prompt
        // Read from standard input. Handle null (EOF) gracefully.
        return readlnOrNull() ?: ""
    }

    override fun toString() = "<native fn input>"
}