package net.morti.klox.interpreter

class LoxClass(
    val name: String,
    private val methods: Map<String, LoxFunction>,
) : LoxCallable {
    override fun call(
        interpreter: Interpreter,
        arguments: List<Any?>,
    ): Any {
        val instance = LoxInstance(this)
        val init = findMethod("init")
        init?.bind(instance)?.call(interpreter, arguments)
        return instance
    }

    fun findMethod(name: String): LoxFunction? {
        if (name in methods) {
            return methods[name]
        }

        return null
    }

    override fun arity(): Int {
        val init = findMethod("init") ?: return 0
        return init.arity()
    }

    override fun toString(): String = name
}
