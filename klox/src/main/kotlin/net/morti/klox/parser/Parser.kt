package net.morti.klox.parser

import net.morti.klox.scanner.Token
import net.morti.generated.klox.parser.Expr
import net.morti.klox.scanner.TokenType
import net.morti.klox.scanner.TokenType.*

class Parser(private val tokens: List<Token>) {
    private var current: Int = 0

    fun parse(): Expr? {
        try {
            return expression()
        } catch (error: ParseError) {
            return null
        }
    }

    private fun expression(): Expr {
        return equality()
    }

    private fun equality(): Expr {
        var expr = comparison()

        while(match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while(match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while(match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun unary(): Expr {
        if(match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }

        return primary()
    }

    private fun primary(): Expr {
        if(match(FALSE)) return Expr.Literal(false)
        if(match(TRUE)) return Expr.Literal(true)
        if(match(NIL)) return Expr.Literal(null)

        if(match(NUMBER, STRING)) return Expr.Literal(previous().literal)

        if(match(LEFT_PAREN)) {
            val expr = expression()
            consume(RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw parseError(peek(), "expected expression.")
    }


    private fun consume(type: TokenType, message: String): Token {
        if(checkType(type)) return advance()

        throw parseError(peek(), message)
    }

    private fun parseError(token: Token, message: String): ParseError {
        return ParseError(token, message)
    }

    private fun match(vararg types: TokenType): Boolean {
        for(type in types) {
            if(checkType(type)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun checkType(type: TokenType): Boolean {
        if(isAtEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if(!isAtEnd()) current++;
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type == EOF
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun synchronize() {
        advance()

        while(!isAtEnd()) {
            if(previous().type == SEMICOLON) return

            when(val t = peek().type) {
                CLASS -> return
                FUN -> return
                VAR -> return
                FOR -> return
                IF -> return
                WHILE -> return
                PRINT -> return
                RETURN -> return
                else -> advance()
            }
        }
    }
}