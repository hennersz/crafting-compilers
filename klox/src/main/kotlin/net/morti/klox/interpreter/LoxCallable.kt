package net.morti.klox.interpreter

interface LoxCallable {
    fun call(
        interpreter: Interpreter,
        arguments: List<Any?>,
    ): Any?
    fun arity(): Int
}
