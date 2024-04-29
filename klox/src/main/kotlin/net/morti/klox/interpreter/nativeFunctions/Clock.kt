package net.morti.klox.interpreter.nativeFunctions

import net.morti.klox.interpreter.Interpreter
import net.morti.klox.interpreter.LocCallable

class Clock : LocCallable {
    override fun call(
        interpreter: Interpreter,
        arguments: List<Any?>,
    ): Any {
        return System.currentTimeMillis().toDouble() / 1000.0
    }

    override fun arity(): Int {
        return 0
    }

    override fun toString(): String {
        return "<native fn - clock>"
    }
}
