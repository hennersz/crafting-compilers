package net.morti.klox.interpreter

import net.morti.generated.klox.parser.Expr
import net.morti.klox.environment.Environment

class LoxFunction(
    private val name: String?,
    private val declaration: Expr.Function,
    private val closure: Environment,
    private val isInitializer: Boolean,
) : LoxCallable {
    override fun call(
        interpreter: Interpreter,
        arguments: List<Any?>,
    ): Any? {
        val environment = Environment(closure)
        for (i in 0..<declaration.params.size) {
            environment.define(declaration.params[i].lexeme, arguments[i])
        }

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            if (isInitializer) return closure.getAt(0, "this")
            return returnValue.value
        }

        if (isInitializer) return closure.getAt(0, "this")
        return null
    }

    override fun arity(): Int = declaration.params.size

    override fun toString(): String {
        if (name == null) return "<fn>"
        return "<fn $name>"
    }

    fun bind(instance: LoxInstance): LoxFunction {
        val environment = Environment(closure)
        environment.define("this", instance)
        return LoxFunction(name, declaration, environment, isInitializer)
    }
}
