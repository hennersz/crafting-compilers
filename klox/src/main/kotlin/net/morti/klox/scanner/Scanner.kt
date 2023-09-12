package net.morti.klox.scanner

import net.morti.klox.scanner.TokenType.*

class Scanner(private val source: String) {
    private val tokens: MutableList<Token> = ArrayList()
    private val scanErrors: MutableList<ScanError> = ArrayList()
    private var start = 0
    private var current = 0
    private var line = 1
    companion object {
        val keywords = hashMapOf(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "print" to PRINT,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE,
        )
    }

    fun scanTokens(): Pair<List<Token>, List<ScanError>> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(EOF, "", null, line))
        return Pair(tokens, scanErrors)
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else if (match('*')) {
                    blockComment()
                } else {
                    addToken(SLASH)
                }
            }

            ' ' -> {}
            '\r' -> {}
            '\t' -> {}
            '\n' -> line++
            '"' -> string()
            else -> {
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                } else {
                    addScanError(c, line)
                }
            }
        }
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun addScanError(c: Char, line: Int) {
        scanErrors.add(ScanError("error scanning character: $c", line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return Char.MIN_VALUE
        return source[current]
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            scanErrors.add(ScanError("Unterminated string", line))
            return
        }

        advance()

        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()

            while (isDigit(peek())) advance()
        }

        addToken(NUMBER, source.substring(start, current).toDouble())
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return Char.MIN_VALUE
        return source[current + 1]
    }

    private fun identifier() {
        while(isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        addToken(keywords.getOrDefault(text, IDENTIFIER))
    }

    private fun isAlpha(c: Char) : Boolean {
        return (c in 'a'..'z') ||
                (c in 'A'..'Z') ||
                c == '_'
    }

    private fun isAlphaNumeric(c: Char) : Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun blockComment() {
        var nesting = 1
        advance()
        while (!isAtEnd() && nesting > 0) {
            var c = advance()
            when (c) {
                '/' -> {
                    if (match('*')) {
                        nesting++
                    }
                }
                '*' -> {
                    if (match('/')) {
                        nesting--
                    }
                }
                '\n' -> line++
            }
        }

        if(isAtEnd() && nesting > 0) {
            scanErrors.add(ScanError("Unterminated block comment.", line))
        }
    }
}