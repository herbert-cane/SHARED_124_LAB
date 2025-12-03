package src.aevumFunctions;

import src.aevumEvaluator.AevumEvaluator2;

interface AevumCallable {
    fun arity() : Int

    fun call(interpreter:AevumEvaluator2, arguments: Any?) : Any?
}
