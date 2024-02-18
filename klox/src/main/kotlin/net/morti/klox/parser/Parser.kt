package net.morti.klox.parser

import net.morti.klox.scanner.Token
import net.morti.generated.klox.parser.Expr
import net.morti.generated.klox.parser.Stmt
import net.morti.klox.scanner.TokenType
import net.morti.klox.scanner.TokenType.*
import kotlin.collections.ArrayList

class Parser(private val tokens: List<Token>) {
    private var current: Int = 0

    fun parse(): Pair<List<Stmt>,List<ParseError>>  {
        val statements = ArrayList<Stmt>()
        val errors = ArrayList<ParseError>()
        while(!isAtEnd()) {
            try {
                statements.add(declaration())
            } catch (e: ParseError) {
                errors.add(e)
                synchronize()
            }
        }

        return Pair(statements, errors)
    }

    private fun declaration(): Stmt {
        if(match(VAR)) return varDeclaration()

        return statement()
    }

    private fun varDeclaration(): Stmt {
        val name = consume(IDENTIFIER, "Expect variable name.")

        val initializer: Expr? = if(match(EQUAL)) {
            expression()
        } else {
            null
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.Var(name, initializer)
    }

    private fun statement(): Stmt {
        if(match(FOR)) return forStatement()
        if (match(IF)) return ifStatement()
        if(match(PRINT)) return printStatement()
        if(match(WHILE)) return whileStatement()
        if(match(LEFT_BRACE)) return Stmt.Block(block())

        return expressionStatement()
    }

    private fun forStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'for'.")

        val initializer = if (match(SEMICOLON)) {
            null
        } else if (match(VAR)) {
            varDeclaration()
        } else {
            expressionStatement()
        }

        val condition = if(!checkType(SEMICOLON)) {
            expression()
        } else {
            Expr.Literal(true)
        }

        consume(SEMICOLON, "Expect ';' after loop condition.")

        val increment = if(!checkType(RIGHT_PAREN)) {
            expression()
        } else {
            null
        }

        consume(RIGHT_PAREN, "Expect ')' after for clauses")

        var body = statement()

        if(increment != null) {
            body = Stmt.Block(
                arrayListOf(
                    body,
                    Stmt.Expression(increment)
                )
            )
        }

        body = Stmt.While(condition, body)
        if(initializer != null) {
            body = Stmt.Block(arrayListOf(initializer, body))
        }

        return body
    }

    private fun whileStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'while'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after condition.")
        val body = statement()

        return Stmt.While(condition, body)
    }

    private fun ifStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'if'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after if condition.")

        val thenBranch = statement()
        val elseBranch = if (match(ELSE)) {
            statement()
        } else {
            null
        }

        return Stmt.If(condition, thenBranch, elseBranch)
    }

    private fun block(): List<Stmt> {
        val statements = ArrayList<Stmt>()

        while(!checkType(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration())
        }

        consume(RIGHT_BRACE, "Expected '}' after block.")
        return statements
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = or()

        if(match(EQUAL)) {
            val equals = previous()
            val value = assignment()

            if(expr is Expr.Variable) {
                val name = expr.name
                return Expr.Assign(name, value)
            }

            throw parseError(equals, "Invalid assignment target.")
        }

        return expr
    }

    private fun or(): Expr {
        var expr = and()

        while(match(OR)) {
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun and(): Expr {
        var expr = equality()

        while (match(AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
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

        if(match(IDENTIFIER)) return Expr.Variable(previous())

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

            when(peek().type) {
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