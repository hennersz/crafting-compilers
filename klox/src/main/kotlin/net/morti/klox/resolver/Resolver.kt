package net.morti.klox.resolver

import net.morti.generated.klox.parser.Expr
import net.morti.generated.klox.parser.Stmt
import net.morti.klox.interpreter.Interpreter
import net.morti.klox.scanner.Token
import java.util.*
import kotlin.collections.HashMap

class Resolver(private val interpreter: Interpreter) : Expr.Visitor<Unit>, Stmt.Visitor<Unit> {
    private val scopes = Stack<MutableMap<String, Boolean>>()

    override fun visitBinaryExpr(expr: Expr.Binary): Unit? {
        resolve(expr.left)
        resolve(expr.right)
        return Unit
    }

    override fun visitCallExpr(expr: Expr.Call): Unit? {
        resolve(expr.callee)
        for (argument in expr.arguments) {
            resolve(argument)
        }

        return Unit
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Unit? {
        resolve(expr.expression)
        return Unit
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Unit? {
        return Unit
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Unit? {
        resolve(expr.right)
        return Unit
    }

    override fun visitVariableExpr(expr: Expr.Variable): Unit? {
        if (!scopes.empty() && !scopes.peek()[expr.name.lexeme]!!) {
            throw Exception("Can't read local variable in its own initializer")
        }

        resolveLocal(expr, expr.name)
        return Unit
    }

    private fun resolveLocal(
        expr: Expr,
        name: Token,
    ) {
        for (i in scopes.size - 1 downTo 0) {
            if (scopes[i].containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size - 1 - i)
            }
        }
    }

    override fun visitAssignExpr(expr: Expr.Assign): Unit? {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
        return Unit
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Unit? {
        resolve(expr.left)
        resolve(expr.right)
        return Unit
    }

    override fun visitFunctionExpr(expr: Expr.Function): Unit? {
        resolveFunction(expr)
        return Unit
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Unit? {
        resolve(stmt.expression)
        return Unit
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Unit? {
        resolve(stmt.expression)
        return Unit
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Unit? {
        if (stmt.value != null) resolve(stmt.value)

        return Unit
    }

    override fun visitVarStmt(stmt: Stmt.Var): Unit? {
        declare(stmt.name)
        if (stmt.initializer != null) {
            resolve(stmt.initializer)
        }
        define(stmt.name)
        return Unit
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Unit? {
        beginScope()
        resolve(stmt.statements)
        endScope()
        return Unit
    }

    override fun visitIfStmt(stmt: Stmt.If): Unit? {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        if (stmt.elseBranch != null) resolve(stmt.elseBranch)
        return Unit
    }

    override fun visitWhileStmt(stmt: Stmt.While): Unit? {
        resolve(stmt.condition)
        resolve(stmt.body)
        return Unit
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Unit? {
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt.function)
        return Unit
    }

    private fun resolveFunction(function: Expr.Function) {
        beginScope()
        for (param in function.params) {
            declare(param)
            define(param)
        }
        resolve(function.body)
        endScope()
    }

    private fun resolve(stmts: List<Stmt>) {
        stmts.forEach { stmt -> resolve(stmt) }
    }

    private fun resolve(stmt: Stmt) {
        stmt.accept(this)
    }

    private fun resolve(expr: Expr) {
        expr.accept(this)
    }

    private fun beginScope() {
        scopes.push(HashMap())
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun declare(name: Token) {
        if (scopes.empty()) return
        scopes.peek()[name.lexeme] = false
    }

    private fun define(name: Token) {
        if (scopes.empty()) return
        scopes.peek()[name.lexeme] = true
    }
}
