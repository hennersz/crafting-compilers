package net.morti.klox.lox

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import com.github.stefanbirkner.systemlambda.SystemLambda.*
import java.io.File

internal class LoxTest {

    @Test
    fun noArgs() {
        val args = arrayOf<String>()
        val expected = 0
        val returnVal = Lox().start(args)
        assertEquals(expected, returnVal)
    }

    @Test
    fun tooManyArgs() {
        val args = arrayOf<String>("too", "many", "args")
        val output = tapSystemOut {
            val returnVal = Lox().start(args)
            assertEquals(64, returnVal)
        }
        assertEquals("Usage: jlox [script]\n", output)
    }

    @Test
    fun runFile() {
        val testScript = File("src/test/resources/helloWorld.lox").absolutePath
        val args = arrayOf(testScript)
        val returnValue = Lox().start(args)
        assertEquals(0, returnValue)
    }

    @Test
    fun runFileWithError() {
        val testScript = File("src/test/resources/badString.lox").absolutePath
        val args = arrayOf(testScript)
        val returnValue = Lox().start(args)
        assertEquals(65, returnValue)
    }
}