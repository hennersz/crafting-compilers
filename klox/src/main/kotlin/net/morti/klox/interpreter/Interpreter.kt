package net.morti.klox.interpreter

import net.morti.generated.klox.parser.Expr
import net.morti.generated.klox.parser.Stmt
import net.morti.klox.environment.Environment
import net.morti.klox.interpreter.nativeFunctions.Clock
import net.morti.klox.scanner.Token
import net.morti.klox.scanner.TokenType

class Interpreter :
    Expr.Visitor<Any>,
    Stmt.Visitor<Unit> {
    private val globals = Environment()
    private var environment = globals
    private val locals = HashMap<Expr, Int>()

    init {
        globals.define("clock", Clock())
    }

    fun interpret(stmts: List<Stmt>) {
        for (stmt in stmts) {
            execute(stmt)
        }
    }

    fun resolve(
        expr: Expr,
        depth: Int,
    ) {
        locals[expr] = depth
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    fun executeBlock(
        statements: List<Stmt>,
        environment: Environment,
    ) {
        val previous = this.environment
        try {
            this.environment = environment
            for (statement in statements) {
                execute(statement)
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double > right as Double
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double >= right as Double
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) < (right as Double)
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double <= right as Double
            }
            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double - right as Double
            }
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double / right as Double
            }
            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                left as Double * right as Double
            }
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    left + right
                } else if (left is String && right is String) {
                    left + right
                } else {
                    throw RuntimeError(expr.operator, "Operands must be 2 numbers or 2 strings")
                }
            }
            // TODO Update types we switch on so only binary types are possible here
            else -> null
        }
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)

        val arguments = ArrayList<Any?>()

        for (argument in expr.arguments) {
            arguments.add(evaluate(argument))
        }

        if (callee !is LoxCallable) {
            throw RuntimeError(expr.paren, "Can only call functions and classes.")
        }

        if (arguments.size != callee.arity()) {
            throw RuntimeError(expr.paren, "Expected ${callee.arity()} arguments but got ${arguments.size}.")
        }

        return callee.call(this, arguments)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        val obj = evaluate(expr.obj)
        if (obj is LoxInstance) {
            return obj.get(expr.name)
        }

        throw RuntimeError(expr.name, "Only instances have properties.")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? = evaluate(expr.expression)

    override fun visitLiteralExpr(expr: Expr.Literal): Any? = expr.value

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }
            else -> null
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? = lookUpVariable(expr.name, expr)

    private fun lookUpVariable(
        name: Token,
        expr: Expr,
    ): Any? {
        val distance = locals[expr]
        return if (distance != null) {
            environment.getAt(distance, name.lexeme)
        } else {
            globals.get(name)
        }
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)

        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }
        return value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        val obj = evaluate(expr.obj)

        if (obj !is LoxInstance) {
            throw RuntimeError(expr.name, "Only instances have fields.")
        }

        val value = evaluate(expr.value)
        obj.set(expr.name, value)
        return value
    }

    override fun visitThisExpr(expr: Expr.This): Any? = lookUpVariable(expr.keyword, expr)

    override fun visitFunctionExpr(expr: Expr.Function): Any? = LoxFunction("Anonymous", expr, environment, false)

    private fun evaluate(expr: Expr): Any? = expr.accept(this)

    private fun isTruthy(any: Any?): Boolean {
        if (any == null) return false
        if (any is Boolean) {
            return any
        }
        return true
    }

    private fun isEqual(
        a: Any?,
        b: Any?,
    ): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false
        return a == b
    }

    private fun checkNumberOperand(
        operator: Token,
        operand: Any?,
    ) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number")
    }

    private fun checkNumberOperands(
        operator: Token,
        left: Any?,
        right: Any?,
    ) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Unit? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Unit? {
        val value = evaluate(stmt.expression) ?: "nil"
        println(value)
        return null
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Unit? {
        val value =
            if (stmt.value != null) {
                evaluate(stmt.value)
            } else {
                null
            }

        throw Return(value)
    }

    override fun visitVarStmt(stmt: Stmt.Var): Unit? {
        val value: Any? =
            if (stmt.initializer != null) {
                evaluate(stmt.initializer)
            } else {
                null
            }
        environment.define(stmt.name.lexeme, value)

        return null
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Unit? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    override fun visitIfStmt(stmt: Stmt.If): Unit? {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }

        return null
    }

    override fun visitWhileStmt(stmt: Stmt.While): Unit? {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }

        return null
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Unit? {
        val name = stmt.name.lexeme
        val function = LoxFunction(name, stmt.function, environment, false)
        environment.define(name, function)
        return null
    }

    override fun visitClassStmt(stmt: Stmt.Class): Unit? {
        environment.define(stmt.name.lexeme, null)

        val methods = HashMap<String, LoxFunction>()
        for (method in stmt.methods) {
            val function = LoxFunction(method.name.lexeme, method.function, environment, method.name.lexeme == "init")
            methods[method.name.lexeme] = function
        }

        val klass = LoxClass(stmt.name.lexeme, methods)
        environment.assign(stmt.name, klass)
        return Unit
    }
}
