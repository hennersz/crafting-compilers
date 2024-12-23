package net.morti.klox.parser

import net.morti.klox.scanner.Token
import net.morti.klox.scanner.TokenType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class ParseErrorTest {
    @Test
    fun testEquals() {
        val a = ParseError(Token(TokenType.IDENTIFIER, "a", null, 1), "Bad identifier")
        val b = ParseError(Token(TokenType.IDENTIFIER, "a", null, 1), "Bad identifier")

        assertEquals(a, b)
    }

    @Test
    fun testNotEquals() {
        val a = ParseError(Token(TokenType.IDENTIFIER, "a", null, 1), "Bad identifier")
        val b = ParseError(Token(TokenType.IDENTIFIER, "b", null, 1), "Bad identifier")

        assertNotEquals(a, b)

        val c = ParseError(Token(TokenType.IDENTIFIER, "c", null, 1), "Bad identifier")
        val d = ParseError(Token(TokenType.IDENTIFIER, "c", null, 1), "another error")

        assertNotEquals(c, d)

        val err = Error()

        assertFalse(c.equals(err))
    }

    @Test
    fun testHash() {
        val a = ParseError(Token(TokenType.IDENTIFIER, "a", null, 1), "Bad identifier")
        val b = ParseError(Token(TokenType.IDENTIFIER, "a", null, 1), "Bad identifier")

        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun testString() {
        val a = ParseError(Token(TokenType.IDENTIFIER, "a", null, 1), "Bad identifier")

        assertEquals("Token: IDENTIFIER a null 1, Message: Bad identifier", a.toString())
    }
}
