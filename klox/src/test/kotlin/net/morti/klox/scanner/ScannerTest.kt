package net.morti.klox.scanner

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ScannerTest {
    @Test
    fun testHelloWorld() {
        val (tokens, errors) = Scanner("print \"Hello, world!\";").scanTokens()
        assertEquals(0, errors.size)
        assertContentEquals(
            arrayListOf(
                Token(TokenType.PRINT, "print", null, 1),
                Token(TokenType.STRING, "\"Hello, world!\"", "Hello, world!", 1),
                Token(TokenType.SEMICOLON, ";", null, 1),
                Token(TokenType.EOF, "", null, 1),
            ),
            tokens
        )
    }

    @Test
    fun testBasicTokens() {
        val (tokens, errors) = Scanner("(){},.-+*!=! == = <= < >= > / \t\r").scanTokens()
        assertEquals(0, errors.size)
        assertContentEquals(
            arrayListOf(
                Token(TokenType.LEFT_PAREN, "(", null, 1),
                Token(TokenType.RIGHT_PAREN, ")", null, 1),
                Token(TokenType.LEFT_BRACE, "{", null, 1),
                Token(TokenType.RIGHT_BRACE, "}", null, 1),
                Token(TokenType.COMMA, ",", null, 1),
                Token(TokenType.DOT, ".", null, 1),
                Token(TokenType.MINUS, "-", null, 1),
                Token(TokenType.PLUS, "+", null, 1),
                Token(TokenType.STAR, "*", null, 1),
                Token(TokenType.BANG_EQUAL, "!=", null, 1),
                Token(TokenType.BANG, "!", null, 1),
                Token(TokenType.EQUAL_EQUAL, "==", null, 1),
                Token(TokenType.EQUAL, "=", null, 1),
                Token(TokenType.LESS_EQUAL, "<=", null, 1),
                Token(TokenType.LESS, "<", null, 1),
                Token(TokenType.GREATER_EQUAL, ">=", null, 1),
                Token(TokenType.GREATER, ">", null, 1),
                Token(TokenType.SLASH, "/", null, 1),
                Token(TokenType.EOF, "", null, 1),
            ),
            tokens
        )
    }

    @Test
    fun testUnknownLexeme() {
        val (tokens, errors) = Scanner("[];").scanTokens()
        assertContentEquals(
            arrayListOf(
                Token(TokenType.SEMICOLON, ";", null, 1),
                Token(TokenType.EOF, "", null, 1),
            ),
            tokens
        )
        assertContentEquals(
            arrayListOf(
                ScanError("error scanning character: [", 1),
                ScanError("error scanning character: ]", 1)
            ),
            errors
        )
    }

    @Test
    fun testMultiLine() {
        val (tokens, errors) = Scanner("""
            print "Hello, world!";
            var im_a_variable = "here is my value";
            var NUMERIC123 = 123;
        """.trimIndent()).scanTokens()
        assertEquals(0, errors.size)
        assertContentEquals(
            arrayListOf(
                Token(TokenType.PRINT, "print", null, 1),
                Token(TokenType.STRING, "\"Hello, world!\"", "Hello, world!", 1),
                Token(TokenType.SEMICOLON, ";", null, 1),
                Token(TokenType.VAR, "var", null, 2),
                Token(TokenType.IDENTIFIER, "im_a_variable", null, 2),
                Token(TokenType.EQUAL, "=", null, 2),
                Token(TokenType.STRING, "\"here is my value\"", "here is my value", 2),
                Token(TokenType.SEMICOLON, ";", null, 2),
                Token(TokenType.VAR, "var", null, 3),
                Token(TokenType.IDENTIFIER, "NUMERIC123", null, 3),
                Token(TokenType.EQUAL, "=", null, 3),
                Token(TokenType.NUMBER, "123", 123.0, 3),
                Token(TokenType.SEMICOLON, ";", null, 3),
                Token(TokenType.EOF, "", null, 3),
            ),
            tokens
        )
    }

    @Test
    fun testBlockComment() {
        val (tokens, errors) = Scanner("""
            /* some 
             * long block comment
             */
            print "Hello, world!";
        """.trimIndent()).scanTokens()
        assertEquals(0, errors.size)
        assertContentEquals(
            arrayListOf(
                Token(TokenType.PRINT, "print", null, 4),
                Token(TokenType.STRING, "\"Hello, world!\"", "Hello, world!", 4),
                Token(TokenType.SEMICOLON, ";", null, 4),
                Token(TokenType.EOF, "", null, 4),
            ),
            tokens
        )
    }

    @Test
    fun testNestedBlockComment() {
        val (tokens, errors) = Scanner("""
            /* some 
             * long block comment
            /* with
             * another / inside 
             */
             */
            print "Hello, world!";
        """.trimIndent()).scanTokens()
        assertEquals(0, errors.size)
        assertContentEquals(
            arrayListOf(
                Token(TokenType.PRINT, "print", null, 7),
                Token(TokenType.STRING, "\"Hello, world!\"", "Hello, world!", 7),
                Token(TokenType.SEMICOLON, ";", null, 7),
                Token(TokenType.EOF, "", null, 7),
            ),
            tokens
        )
    }

    @Test
    fun testUnterminatedBlockComment() {
        val (tokens, errors) = Scanner("""
            /* some 
             * long block comment
            /* with
             * another inside 
             */
            print "Hello, world!";
        """.trimIndent()).scanTokens()
        assertContentEquals(
            arrayListOf(
                Token(TokenType.EOF, "", null, 6),
            ),
            tokens
        )
        assertContentEquals(
            arrayListOf(
                ScanError("Unterminated block comment.", 6)
            ),
            errors
        )
    }

    @Test
    fun testUnterminatedString() {
        val (tokens, errors) = Scanner("""
            print "Hello, world!
        """.trimIndent()).scanTokens()
        assertContentEquals(
            arrayListOf(
                Token(TokenType.PRINT, "print", null, 1),
                Token(TokenType.EOF, "", null, 1),
            ),
            tokens
        )
        assertContentEquals(
            arrayListOf(
                ScanError("Unterminated string", 1)
            ),
            errors
        )
    }

    @Test
    fun testDecimal() {
        val (tokens, errors) = Scanner("""
            var test = 1.23;
        """.trimIndent()).scanTokens()
        assertEquals(0, errors.size)
        assertContentEquals(
            arrayListOf(
                Token(TokenType.VAR, "var", null, 1),
                Token(TokenType.IDENTIFIER, "test", null, 1),
                Token(TokenType.EQUAL, "=", null, 1),
                Token(TokenType.NUMBER, "1.23", 1.23, 1),
                Token(TokenType.SEMICOLON, ";", null, 1),
                Token(TokenType.EOF, "", null, 1),
            ),
            tokens
        )
    }

    @Test
    fun testLineComment() {
        val (tokens, errors) = Scanner("""
            // this is a comment
            var test;
            //
        """.trimIndent()).scanTokens()
        assertEquals(0, errors.size)
        assertContentEquals(
            arrayListOf(
                Token(TokenType.VAR, "var", null, 2),
                Token(TokenType.IDENTIFIER, "test", null, 2),
                Token(TokenType.SEMICOLON, ";", null, 2),
                Token(TokenType.EOF, "", null, 3),
            ),
            tokens
        )
    }
}