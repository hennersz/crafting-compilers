package net.morti.klox.resolver

import net.morti.generated.klox.parser.Expr
import net.morti.generated.klox.parser.Stmt
import net.morti.klox.interpreter.Interpreter
import net.morti.klox.scanner.Token
import java.util.*
import kotlin.collections.HashMap

class Resolver(
    private val interpreter: Interpreter,
) : Expr.Visitor<Unit>,
    Stmt.Visitor<Unit> {
    private val scopes = Stack<MutableMap<String, Boolean>>()
    private var currentFunction = FunctionType.NONE
    private var currentClass = ClassType.NONE
    private val errors = ArrayList<ResolutionError>()

    fun resolve(stmts: List<Stmt>): List<ResolutionError> {
        try {
            resolveInner(stmts)
        } catch (error: ResolutionError) {
            errors.add(error)
        }
        return errors
    }

    private fun resolveInner(stmts: List<Stmt>) {
        stmts.forEach { stmt ->
            resolveInner(stmt)
        }
    }

    private fun resolveInner(stmt: Stmt) {
        stmt.accept(this)
    }

    private fun resolveInner(expr: Expr) {
        expr.accept(this)
    }

    private fun resolveFunction(
        function: Expr.Function,
        type: FunctionType,
    ) {
        val enclosingFunction = currentFunction
        currentFunction = type
        beginScope()
        for (param in function.params) {
            declare(param)
            define(param)
        }
        resolveInner(function.body)
        endScope()
        currentFunction = enclosingFunction
    }

    private fun resolveLocal(
        expr: Expr,
        name: Token,
    ) {
        for (i in scopes.size - 1 downTo 0) {
            if (scopes[i].containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size - 1 - i)
                return
            }
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Unit? {
        resolveInner(expr.left)
        resolveInner(expr.right)
        return Unit
    }

    override fun visitCallExpr(expr: Expr.Call): Unit? {
        resolveInner(expr.callee)
        for (argument in expr.arguments) {
            resolveInner(argument)
        }

        return Unit
    }

    override fun visitGetExpr(expr: Expr.Get): Unit? {
        resolveInner(expr.obj)
        return null
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Unit? {
        resolveInner(expr.expression)
        return Unit
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Unit? = Unit

    override fun visitUnaryExpr(expr: Expr.Unary): Unit? {
        resolveInner(expr.right)
        return Unit
    }

    override fun visitVariableExpr(expr: Expr.Variable): Unit? {
        if (!scopes.empty() && scopes.peek()[expr.name.lexeme] == false) {
            throw ResolutionError(expr.name, "Can't read local variable in its own initializer")
        }

        resolveLocal(expr, expr.name)
        return Unit
    }

    override fun visitAssignExpr(expr: Expr.Assign): Unit? {
        resolveInner(expr.value)
        resolveLocal(expr, expr.name)
        return Unit
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Unit? {
        resolveInner(expr.left)
        resolveInner(expr.right)
        return Unit
    }

    override fun visitSetExpr(expr: Expr.Set): Unit? {
        resolveInner(expr.value)
        resolveInner(expr.obj)
        return Unit
    }

    override fun visitThisExpr(expr: Expr.This): Unit? {
        if (currentClass == ClassType.NONE) {
            throw ResolutionError(expr.keyword, "Cant' use 'this' outside of a class.")
        }
        resolveLocal(expr, expr.keyword)
        return Unit
    }

    override fun visitFunctionExpr(expr: Expr.Function): Unit? {
        resolveFunction(expr, FunctionType.FUNCTION)
        return Unit
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Unit? {
        resolveInner(stmt.expression)
        return Unit
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Unit? {
        resolveInner(stmt.expression)
        return Unit
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Unit? {
        if (currentFunction == FunctionType.NONE) {
            throw ResolutionError(stmt.keyword, "Can't return from top level code.")
        }
        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                throw ResolutionError(stmt.keyword, "Can't return a value from an initializer")
            }
            resolveInner(stmt.value)
        }

        return Unit
    }

    override fun visitVarStmt(stmt: Stmt.Var): Unit? {
        declare(stmt.name)
        if (stmt.initializer != null) {
            resolveInner(stmt.initializer)
        }
        define(stmt.name)
        return Unit
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Unit? {
        beginScope()
        resolveInner(stmt.statements)
        endScope()
        return Unit
    }

    override fun visitIfStmt(stmt: Stmt.If): Unit? {
        resolveInner(stmt.condition)
        resolveInner(stmt.thenBranch)
        if (stmt.elseBranch != null) resolveInner(stmt.elseBranch)
        return Unit
    }

    override fun visitWhileStmt(stmt: Stmt.While): Unit? {
        resolveInner(stmt.condition)
        resolveInner(stmt.body)
        return Unit
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Unit? {
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt.function, FunctionType.FUNCTION)
        return Unit
    }

    override fun visitClassStmt(stmt: Stmt.Class): Unit? {
        val enclosingClass = currentClass
        currentClass = ClassType.CLASS
        declare(stmt.name)
        define(stmt.name)

        beginScope()
        scopes.peek()["this"] = true
        for (method in stmt.methods) {
            var declaration = FunctionType.METHOD
            if (method.name.lexeme == "init") {
                declaration = FunctionType.INITIALIZER
            }

            resolveFunction(method.function, declaration)
        }
        endScope()

        currentClass = enclosingClass
        return Unit
    }

    private fun beginScope() {
        scopes.push(HashMap())
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun declare(name: Token) {
        if (scopes.empty()) return
        val scope = scopes.peek()
        if (scope.containsKey(name.lexeme)) {
            throw ResolutionError(name, "Already a variable with this name in scope")
        }

        scope[name.lexeme] = false
    }

    private fun define(name: Token) {
        if (scopes.empty()) return
        scopes.peek()[name.lexeme] = true
    }
}
