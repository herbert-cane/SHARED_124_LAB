package src.aevumEvaluator

import src.ast.Expr
import src.ast.Stmt
import src.aevumEnvironment.Environment
import src.aevumFunctions.*
import src.tokenType.TokenType.*

class AevumEvaluator2 {

    // The global environment (holds native functions)
    private val globals = Environment()
    // The current environment (changes as we enter scopes)
    private var environment = globals

    init {
        // === [Lab 5] Define native functions ===
        // Example: clock() returns current time in seconds

        // Aevum Native Functions
        globals.define("input", NativeInput())

        // Dialogue and Character Keywords
        globals.define("speak", NativeSpeak())
        globals.define("say", NativeSay())
        globals.define("character", NativeCharacter())

        // Choices and Branching
        globals.define("choice", NativeChoice())
        globals.define("option", NativeOption())
        globals.define("spawn", NativeSpawn())

        // Game Flow
        globals.define("start", NativeStart())
        globals.define("continue", NativeContinue())
        globals.define("restart", NativeRestart())
        globals.define("endgame", NativeEndgame())

        // Actions & Triggers
        globals.define("action", NativeAction())
        globals.define("trigger", NativeTrigger())
        globals.define("win", NativeWin())
        globals.define("lose", NativeLose())

        // Inventory System
        globals.define("add", NativeAdd())
        globals.define("inventory", NativeInventory())
        globals.define("use", NativeUse())
        globals.define("item", NativeItem())

        // Stats System
        globals.define("setStat", NativeSetStat())
        globals.define("modStat", NativeModStat())
        globals.define("getStat", NativeGetStat())
        globals.define("checkStats", NativeCheckStats())

        // Auxiliary
        globals.define("random", NativeRandom())
    }
    // Accept the Root Program Node
    fun interpret(program: Stmt.Program) {
        try {
            if (program.body != null) {
                execute(program.body)
            }
        } catch (_: RuntimeErrorHandler.RuntimeError) {
            // catch logic
        }
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            // === [Lab 5] Control Flow ===
            is Stmt.If -> {
                if (EvaluatorUtils.isTruthy(evaluate(stmt.condition))) {
                    execute(stmt.thenBranch)
                } else if (stmt.elseBranch != null) {
                    execute(stmt.elseBranch)
                }
            }
            is Stmt.While -> {
                while (EvaluatorUtils.isTruthy(evaluate(stmt.condition))) {
                    execute(stmt.body)
                }
            }

            // === [Lab 5] Functions ===
            is Stmt.Function -> {
                // Capture the current environment (Closure)
                val function = AevumFunction(stmt, environment)
                environment.define(stmt.name.lexeme, function)
            }
            is Stmt.Return -> {
                var value: Any? = null
                if (stmt.value != null) value = evaluate(stmt.value)
                // Throw exception to unwind stack
                throw Return(value)
            }

            // The Sequence Logic (Traversing the tree)
            is Stmt.Sequence -> {
                execute(stmt.first) // Execute Left Child
                execute(stmt.next)  // Execute Right Child
            }

            // Program and Block now just unwrap the body
            is Stmt.Program -> if (stmt.body != null) execute(stmt.body)
            is Stmt.Block -> {
                if (stmt.body != null) {
                    executeBlock(stmt.body, Environment(environment))
                }
            }
            is Stmt.Expression -> evaluate(stmt.expression)
            is Stmt.Print -> {
                val value = evaluate(stmt.expression)
                println(EvaluatorUtils.stringify(value))
            }
            is Stmt.Var -> {
                var value: Any? = null
                if (stmt.initializer != null) {
                    value = evaluate(stmt.initializer)
                }
                environment.define(stmt.name.lexeme, value)
            }
        }
    }

    // Updated Block Execution
    internal fun executeBlock(body: Stmt, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            execute(body) // This will trigger the Sequence traversal recursively
        } finally {
            this.environment = previous
        }
    }
    private fun evaluate(expr: Expr): Any? {
        return when (expr) {
            // === [Lab 5] Logic & Calls ===
            is Expr.Logical -> {
                val left = evaluate(expr.left)

                // Short-circuiting Logic
                if (expr.operator.type == OR) {
                    if (EvaluatorUtils.isTruthy(left)) return left
                } else {
                    // AND
                    if (!EvaluatorUtils.isTruthy(left)) return left
                }

                return evaluate(expr.right)
            }
            is Expr.FunctionCall -> {
                val callee = evaluate(expr.callee)
                val argumentTree = evaluateArgumentTree(expr.arguments)

                if (callee !is AevumCallable) {
                    throw RuntimeErrorHandler.report(expr.paren, "Can only call functions and classes.")
                }

                // === [Lab 5] Arity checking with clear error messages ===
                val argCount = EvaluatorUtils.countArgs(argumentTree)
                if (argCount != callee.arity()) {
                    throw RuntimeErrorHandler.report(
                        expr.paren,
                        "Expected ${callee.arity()} arguments but got $argCount."
                    )
                }

                callee.call(this, argumentTree)
            }
            is Expr.Literal -> expr.value
            is Expr.Grouping -> evaluate(expr.expression)
            is Expr.Unary -> evaluateUnary(expr)
            is Expr.Binary -> evaluateBinary(expr)
            is Expr.Variable -> environment.get(expr.name)
            is Expr.Assign -> {
                val value = evaluate(expr.value)
                environment.assign(expr.name, value)
                value
            }
        }
    }

    // === [Lab 5] Evaluating an Argument Tree (No listing) ===
    private fun evaluateArgumentTree(expr: Expr?): Any? {
        if (expr == null) return null

        // If it's a Comma (List Separator), preserve the tree structure using Pairs
        if (expr is Expr.Binary && expr.operator.type == COMMA) {
            val leftVal = evaluate(expr.left)
            val rightVal = evaluateArgumentTree(expr.right)
            return Pair(leftVal, rightVal)
        }

        // It's a single leaf value
        return evaluate(expr)
    }
    private fun flattenArgs(expr: Expr?, list: MutableList<Any?>) {
        if (expr == null) return

        // Arithmetic expressions (like n - 1) are Binary but lists
        // We only treat it as a list if the operator is a COMMA
        if (expr is Expr.Binary && expr.operator.type == COMMA) {
            // It's a tree: Left is value, Right is next node
            list.add(evaluate(expr.left))
            flattenArgs(expr.right, list)
        } else {
            // It's a single leaf
            list.add(evaluate(expr))
        }
    }

    private fun evaluateUnary(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            MINUS -> {
                // [Use Helper]
                EvaluatorUtils.checkNumberOperand(expr.operator, right)
                -(right as Double)
            }
            NOT -> !EvaluatorUtils.isTruthy(right) // [Use Helper]
            else -> null
        }
    }

    private fun evaluateBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            PLUS -> {
                // If both are numbers, add them
                if (left is Double && right is Double) {
                    return left + right
                }

                // If EITHER is a string, concatenate them as strings
                if (left is String || right is String) {
                    // [Use Helper]
                    return EvaluatorUtils.stringify(left) + EvaluatorUtils.stringify(right)
                }

                throw RuntimeErrorHandler.report(expr.operator, "Operands must be two numbers or two strings.")
            }
            MINUS -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) - (right as Double)
            }
            STAR -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) * (right as Double)
            }
            SLASH -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                val r = (right as Double)
                if (r == 0.0) throw RuntimeErrorHandler.report(expr.operator, "Division by zero.")
                (left as Double) / r
            }
            GREATER -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) > (right as Double)
            }
            GREATER_EQUAL -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) >= (right as Double)
            }
            LESS -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }
            LESS_EQUAL -> {
                EvaluatorUtils.checkNumberOperands(expr.operator, left, right)
                (left as Double) <= (right as Double)
            }
            EQUAL_EQUAL -> EvaluatorUtils.isEqual(left, right)
            NOT_EQUAL -> !EvaluatorUtils.isEqual(left, right)
            else -> null
        }
    }
}

