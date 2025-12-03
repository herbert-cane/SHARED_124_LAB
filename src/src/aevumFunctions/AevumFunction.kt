package src.aevumFunctions

import src.aevumEvaluator.AevumEvaluator2
import src.ast.Stmt
import src.ast.Expr
import src.aevumEnvironment.Environment

class AevumFunction(private val declaration: Stmt.Function, private val closure: Environment) : AevumCallable {

    override fun arity(): Int {
        return countParameters(declaration.parameters)
    }

    private fun countParameters(expr: Expr?): Int {
        if (expr == null) return 0
        if (expr is Expr.Binary) {
            return countParameters(expr.left) + countParameters(expr.right)
        }
        return 1
    }

    override fun call(interpreter: AevumEvaluator2, arguments: Any?): Any? {
        val environment = Environment(closure)

        bindParameters(declaration.parameters, arguments, environment)

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }
        return null
    }

    private fun bindParameters(parameterExpr: Expr?, argValue: Any?, env: Environment) {
        if (parameterExpr == null) return

        if (parameterExpr is Expr.Binary) {
            val pair = argValue as? Pair<*, *>

            bindParameters(parameterExpr.left, pair?.first, env)
            bindParameters(parameterExpr.right, pair?.second, env)

        } else if (parameterExpr is Expr.Variable) {
            env.define(parameterExpr.name.lexeme, argValue)
        }
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}