package net.morti.klox.interpreter

interface LocCallable {
    fun call(
        interpreter: Interpreter,
        arguments: List<Any?>,
    ): Any?
    fun arity(): Int
}
