package net.morti.klox.interpreter.nativeFunctions

import net.morti.klox.interpreter.Interpreter
import net.morti.klox.interpreter.LoxCallable

class Clock : LoxCallable {
    override fun call(
        interpreter: Interpreter,
        arguments: List<Any?>,
    ): Any = System.currentTimeMillis().toDouble() / 1000.0

    override fun arity(): Int = 0

    override fun toString(): String = "<native fn - clock>"
}
