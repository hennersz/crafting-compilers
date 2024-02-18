package net.morti.klox.interpreter

import net.morti.generated.klox.parser.Expr
import net.morti.generated.klox.parser.Stmt
import net.morti.klox.environment.Environment
import net.morti.klox.scanner.Token
import net.morti.klox.scanner.TokenType

class Interpreter: Expr.Visitor<Any>, Stmt.Visitor<Unit> {
    private var environment = Environment()

    fun interpret(stmts: List<Stmt>) {
            for (stmt in stmts) {
                execute(stmt)
            }
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when(expr.operator.type) {
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
                if(left is Double && right is Double) {
                    left + right
                } else if (left is String && right is String) {
                    left + right
                } else {
                    throw RuntimeError(expr.operator, "Operands must be 2 numbers or 2 strings")
                }
            }
            else -> null
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when(expr.operator.type) {
            TokenType.BANG -> !isTruthy(right)
            TokenType.MINUS ->{
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }
            else -> null
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return environment.get(expr.name)
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun isTruthy(any: Any?): Boolean {
        if (any == null) return false
        if (any is Boolean) return any
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if(a == null && b == null) return true
        if(a == null) return false
        return a == b
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if(left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Unit?{
        evaluate(stmt.expression)
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Unit? {
        val value = evaluate(stmt.expression)?: "nil"
        println(value)
        return null
    }

    override fun visitVarStmt(stmt: Stmt.Var): Unit? {
        val value: Any? = if(stmt.initializer != null) {
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

    private fun executeBlock(statements: List<Stmt>, environment: Environment) {
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
}