package net.morti.klox.environment

import net.morti.klox.interpreter.RuntimeError
import net.morti.klox.scanner.Token

class Environment(private val enclosing: Environment?) {
    constructor() : this(null)

    private val values = HashMap<String, Any?>()

    fun define(
        name: String,
        value: Any?,
    ) {
        values[name] = value
    }

    fun get(name: Token): Any? {
        if (name.lexeme in values) {
            return values[name.lexeme]
        }

        if (enclosing != null) {
            return enclosing.get(name)
        }

        throw RuntimeError(name, "Undefined variable at '${name.lexeme}'.")
    }

    fun assign(
        name: Token,
        value: Any?,
    ) {
        if (name.lexeme in values) {
            values[name.lexeme] = value
            return
        }

        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    fun getAt(
        distance: Int,
        name: String,
    ): Any? {
        return ancestor(distance)?.values?.get(name)
    }

    private fun ancestor(distance: Int): Environment? {
        var environment: Environment? = this
        repeat(distance) {
            environment = environment?.enclosing
        }

        return environment
    }

    fun assignAt(
        distance: Int,
        name: Token,
        value: Any?,
    ) {
        ancestor(distance)?.values?.put(name.lexeme, value)
    }
}
