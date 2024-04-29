package net.morti.klox.lox

import net.morti.generated.klox.parser.Expr

class AstPrinter : Expr.Visitor<String> {
    fun print(expr: Expr): String? {
        return expr.accept(this)
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String? {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitCallExpr(expr: Expr.Call): String? {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String? {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        if (expr.value == null) return "nil"
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String? {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    override fun visitVariableExpr(expr: Expr.Variable): String? {
        TODO("Not yet implemented")
    }

    override fun visitAssignExpr(expr: Expr.Assign): String? {
        TODO("Not yet implemented")
    }

    override fun visitLogicalExpr(expr: Expr.Logical): String? {
        TODO("Not yet implemented")
    }

    override fun visitFunctionExpr(expr: Expr.Function): String? {
        TODO("Not yet implemented")
    }

    private fun parenthesize(
        name: String,
        vararg exprs: Expr,
    ): String? {
        return "($name ${exprs.joinToString(" ") { expr -> expr.accept(this).orEmpty() }})"
    }
}
