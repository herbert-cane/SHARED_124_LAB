package src.aevumFunctions

import src.aevumEvaluator.AevumEvaluator2
import src.aevumEvaluator.EvaluatorUtils

// === [Lab 5] Native Functions ===

/**
 * Native implementation of 'speak'.
 * Used for general narration or unassigned dialogue.
 *
 * Usage: speak("The wind howls...")
 * Output: The wind howls...
 *
 * Arity: 1 -> Since there is only 1 argument, the 'arguments' parameter
 * holds the value directly (not wrapped in a Pair).
 */
class NativeSpeak : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val message = EvaluatorUtils.stringify(arguments)
        println(message)
        return null
    }
}

/**
 * Native implementation of 'say'.
 * Used for character-specific dialogue, formatted with the speaker's name.
 *
 * Usage: say("Hero", "Let's go!")
 * Output: Hero: "Let's go!"
 *
 * Arity: 2 -> Arguments are passed as a Nested Pair Tree: Pair(Name, Message).
 */
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
 * Native implementation of 'character'.
 * Defines a scene context or location change.
 *
 * Usage: character("The Dark Forest")
 * Output: === SCENE: The Dark Forest ===
 * Arity: 1 -> Arguments received as the direct value.
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

/**
 * Native implementation of 'choice'.
 * Displays a major decision point to the player.
 *
 * Usage: choice("What will you do?")
 * Output: ??? CHOICE: What will you do? ???
 * Arity: 1 -> Arguments received as the direct value.
 */
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
 * Displays a specific option within a choice block.
 *
 * Usage: option(1, "Attack the monster")
 * Output:    [1] Attack the monster
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
 * Simulates an entity appearing in the game world.
 *
 * Usage: spawn("Enemy", "Goblin")
 * Output: +++ SPAWN: Goblin (Enemy) appeared! +++
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
 * Pauses execution to read a line of text from the user.
 *
 * Usage: var selection = input();
 * Output: > [Waits for input]
 * Arity: 0 -> Ignores arguments.
 * Returns: The string typed by the user, or empty string on EOF.
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
/**
 * Native implementation of 'start'.
 * Signals the beginning of a new game or major story arc.
 *
 * Usage: start("The Quest Begins")
 * Output: === START: The Quest Begins ===
 * Arity: 1 -> Arguments received as the direct value.
 */
class NativeStart : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val title = EvaluatorUtils.stringify(arguments)
        println("\n========================================")
        println("=== START: $title ===")
        println("========================================\n")
        return null
    }

    override fun toString() = "<native fn start>"
}

/**
 * Native implementation of 'continue'.
 * Used to transition between scenes or pause for effect.
 *
 * Usage: continue("Press Enter to continue...")
 * Output: >>> Press Enter to continue... >>>
 * Arity: 1 -> Arguments received as the direct value.
 */
class NativeContinue : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val message = EvaluatorUtils.stringify(arguments)
        println("\n>>> $message >>>")
        // Optional: We could pause here like input(), but for now just printing the transition
        return null
    }

    override fun toString() = "<native fn continue>"
}

/**
 * Native implementation of 'restart'.
 * Signals a reset of the game state (conceptually).
 *
 * Usage: restart();
 * Output: === SYSTEM: RESTARTING GAME ===
 * Arity: 0
 */
class NativeRestart : AevumCallable {
    override fun arity(): Int = 0

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        println("\n\u21BB \u21BB \u21BB SYSTEM: RESTARTING GAME \u21BB \u21BB \u21BB\n")
        return null
    }

    override fun toString() = "<native fn restart>"
}

/**
 * Native implementation of 'endgame'.
 * Signals the final conclusion of the story.
 *
 * Usage: endgame("Thanks for playing!");
 * Output: *** GAME OVER: Thanks for playing! ***
 * Arity: 1
 */
class NativeEndgame : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val message = EvaluatorUtils.stringify(arguments)
        println("\n****************************************")
        println("*** GAME OVER: $message ***")
        println("****************************************\n")

        // Optional: We could use System.exit(0) here if we want to forcefully close the program.
        // For the REPL, it is safer to just print.
        return null
    }

    override fun toString() = "<native fn endgame>"
}

/**
 * Native implementation of 'action'.
 * Represents a major player activity like fighting or exploring.
 *
 * Usage: action("Fight")
 * Output: *** ACTION: Fight ***
 * Arity: 1 -> Arguments received as the direct value.
 */
class NativeAction : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val actionName = EvaluatorUtils.stringify(arguments)
        println("\n*** ACTION: $actionName ***")
        return null
    }

    override fun toString() = "<native fn action>"
}

/**
 * Native implementation of 'trigger'.
 * Signals that a game event or trap has been activated.
 *
 * Usage: trigger("Trap triggered!")
 * Output: [!] EVENT: Trap triggered! [!]
 * Arity: 1 -> Arguments received as the direct value.
 */
class NativeTrigger : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val eventName = EvaluatorUtils.stringify(arguments)
        println("[!] EVENT: $eventName [!]")
        return null
    }

    override fun toString() = "<native fn trigger>"
}

/**
 * Native implementation of 'win'.
 * A specific game-ending state for victory.
 *
 * Usage: win("You defeated the dragon!");
 * Output: \o/ VICTORY: You defeated the dragon! \o/
 * Arity: 1
 */
class NativeWin : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val message = EvaluatorUtils.stringify(arguments)
        println("\n\\o/ VICTORY: $message \\o/")
        // Note: In a real game engine, this might set a flag to stop the game loop.
        // For the lab, printing is sufficient.
        return null
    }

    override fun toString() = "<native fn win>"
}

/**
 * Native implementation of 'lose'.
 * A specific game-ending state for defeat.
 *
 * Usage: lose("You died.");
 * Output: /!\ DEFEAT: You died. /!\
 * Arity: 1
 */
class NativeLose : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val message = EvaluatorUtils.stringify(arguments)
        println("\n/!\\ DEFEAT: $message /!\\")
        return null
    }

    override fun toString() = "<native fn lose>"
}

/**
 * SHARED STATE for the Inventory System.
 * This acts as the "Backpack" that persists across function calls.
 */
object InventoryState {
    val items = mutableListOf<String>()
}

/**
 * Native implementation of 'add'.
 * Adds an item to the player's persistent inventory.
 *
 * Usage: add("Potion")
 * Output: + Added Potion to inventory.
 * Arity: 1
 */
class NativeAdd : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val item = EvaluatorUtils.stringify(arguments)
        InventoryState.items.add(item)
        println("+ Added $item to inventory.")
        return null
    }

    override fun toString() = "<native fn add>"
}

/**
 * Native implementation of 'inventory'.
 * Lists all items currently held by the player.
 *
 * Usage: inventory()
 * Output:
 * === INVENTORY ===
 * - Potion
 * - Sword
 *
 * Arity: 0
 */
class NativeInventory : AevumCallable {
    override fun arity(): Int = 0

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        println("\n=== INVENTORY ===")
        if (InventoryState.items.isEmpty()) {
            println("(empty)")
        } else {
            for (item in InventoryState.items) {
                println("- $item")
            }
        }
        println("=================\n")
        return null
    }

    override fun toString() = "<native fn inventory>"
}

/**
 * Native implementation of 'use'.
 * Consumes an item if it exists in the inventory.
 * Returns: TRUE if successful, FALSE if item not found.
 */
class NativeUse : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val item = EvaluatorUtils.stringify(arguments)

        if (InventoryState.items.contains(item)) {
            InventoryState.items.remove(item)
            println("> Used $item.")
            return true // Return success
        } else {
            println("! You do not have $item in your inventory.")
            return false // Return failure
        }
    }

    override fun toString() = "<native fn use>"
}

/**
 * Native implementation of 'item'.
 * Describes an item's details (Flavor text).
 *
 * Usage: item("Sword", "A rusty blade.")
 * Output: [ITEM] Sword: A rusty blade.
 * Arity: 2 -> Pair(Name, Description)
 */
class NativeItem : AevumCallable {
    override fun arity(): Int = 2

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val pair = arguments as? Pair<*, *>
        val name = EvaluatorUtils.stringify(pair?.first)
        val desc = EvaluatorUtils.stringify(pair?.second)

        println("[ITEM] $name: $desc")
        return null
    }

    override fun toString() = "<native fn item>"
}

/**
 * SHARED STATE for the Stats System.
 * Maps "Character Name" -> { "HP" -> 100, "ATK" -> 15 }
 */
object StatsSystem {
    val characters = mutableMapOf<String, MutableMap<String, Int>>()

    fun get(name: String, stat: String): Int {
        return characters[name]?.get(stat) ?: 0
    }

    fun set(name: String, stat: String, value: Int) {
        if (!characters.containsKey(name)) {
            characters[name] = mutableMapOf()
        }
        characters[name]!![stat] = value
    }

    fun modify(name: String, stat: String, amount: Int) {
        val current = get(name, stat)
        set(name, stat, current + amount)
    }
}

/**
 * Native implementation of 'setStat'.
 * Defines a specific stat for a character.
 */
class NativeSetStat : AevumCallable {
    override fun arity(): Int = 3

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val pair1 = arguments as? Pair<*, *>
        val charName = EvaluatorUtils.stringify(pair1?.first)

        val pair2 = pair1?.second as? Pair<*, *>
        val statName = EvaluatorUtils.stringify(pair2?.first)

        // [FIX] Robustly handle Numbers (Int or Double)
        val rawValue = pair2?.second
        val value = (rawValue as? Number)?.toDouble() ?: 0.0

        StatsSystem.set(charName, statName, value.toInt())
        println("[STATS] $charName $statName set to ${value.toInt()}.")
        return null
    }
    override fun toString() = "<native fn setStat>"
}

/**
 * Native implementation of 'modStat'.
 * Modifies a stat (Growth, Damage, Buffs).
 *
 * [Smart Capping Feature]:
 * If you modify a stat (e.g. "HP"), this function checks if a "MAX_HP" stat exists.
 * If it does, the new value is capped at that maximum.
 */
class NativeModStat : AevumCallable {
    override fun arity(): Int = 3

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val pair1 = arguments as? Pair<*, *>
        val charName = EvaluatorUtils.stringify(pair1?.first)

        val pair2 = pair1?.second as? Pair<*, *>
        val statName = EvaluatorUtils.stringify(pair2?.first)
        val rawValue = pair2?.second

        // Robust conversion
        val amount = (rawValue as? Number)?.toDouble() ?: 0.0
        val current = StatsSystem.get(charName, statName)
        var newValue = current + amount.toInt()

        // --- SMART CAPPING LOGIC ---
        // Check if a "MAX_" version of this stat exists (e.g. MAX_HP for HP)
        val maxStatName = "MAX_$statName"
        val charStats = StatsSystem.characters[charName]

        if (charStats != null && charStats.containsKey(maxStatName)) {
            val maxVal = charStats[maxStatName]!!
            if (newValue > maxVal) {
                newValue = maxVal
                // Optional: Let the user know they hit the cap?
                // println("(Capped at $maxVal)")
            }
        }
        // ---------------------------

        StatsSystem.set(charName, statName, newValue)
        println("[STATS] $charName $statName modified by ${amount.toInt()} (New: $newValue).")
        return null
    }

    override fun toString() = "<native fn modStat>"
}

/**
 * Native implementation of 'getStat'.
 * Returns the numeric value of a stat (for logic checks).
 *
 * Usage: var hp = getStat("Hero", "HP");
 * Arity: 2 -> Pair(Name, Stat)
 */
class NativeGetStat : AevumCallable {
    override fun arity(): Int = 2

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val pair = arguments as? Pair<*, *>
        val charName = EvaluatorUtils.stringify(pair?.first)
        val statName = EvaluatorUtils.stringify(pair?.second)

        return StatsSystem.get(charName, statName).toDouble()
    }
    override fun toString() = "<native fn getStat>"
}

/**
 * Native implementation of 'checkStats'.
 * Debug function to view a character's full status.
 *
 * Usage: checkStats("Hero")
 * Output:
 * === STATUS: Hero ===
 * HP: 100
 * ATK: 15
 * DEF: 5
 * ====================
 * Arity: 1
 */
class NativeCheckStats : AevumCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val charName = EvaluatorUtils.stringify(arguments)
        val stats = StatsSystem.characters[charName]

        println("\n=== STATUS: $charName ===")
        if (stats == null || stats.isEmpty()) {
            println("(No stats defined)")
        } else {
            for ((k, v) in stats) {
                println("$k: $v")
            }
        }
        println("====================\n")
        return null
    }
    override fun toString() = "<native fn checkStats>"
}

/**
 * Native implementation of 'random'.
 * Generates a random integer between min and max (inclusive).
 *
 * Usage: var dmg = random(5, 10);
 * Arity: 2 -> Pair(Min, Max)
 */
class NativeRandom : AevumCallable {
    override fun arity(): Int = 2

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val pair = arguments as? Pair<*, *>

        // Robustly handle number inputs
        val min = (pair?.first as? Number)?.toInt() ?: 0
        val max = (pair?.second as? Number)?.toInt() ?: 10

        return (min..max).random().toDouble()
    }

    override fun toString() = "<native fn random>"
}