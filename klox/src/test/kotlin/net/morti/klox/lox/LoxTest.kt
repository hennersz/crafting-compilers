package net.morti.klox.lox

import com.github.stefanbirkner.systemlambda.SystemLambda.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*
import kotlin.test.assertEquals

class LoxTest {
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
        val output =
            tapSystemOut {
                val returnVal = Lox().start(args)
                assertEquals(64, returnVal)
            }
        assertEquals("Usage: jlox [script]\n", output)
    }

    @TestFactory
    fun runTestFiles(): List<DynamicTest> =
        Paths.get("../tests/resources").listDirectoryEntries("*.lox").map { path ->
            dynamicTest(path.fileName.toString()) {
                val testScript = path.toFile().absolutePath
                val expectedOutput = Path(testScript.replace(".lox", ".out")).readText().trim()
                val returnFile = Path(testScript.replace(".lox", ".ret"))
                val expectedReturnValue =
                    if (returnFile.exists()) {
                        returnFile.readText().trim().toInt()
                    } else {
                        0
                    }
                var returnValue = -1
                val output =
                    tapSystemErrAndOut {
                        returnValue = Lox().start(arrayOf(testScript))
                    }.trim()
                assertEquals(expectedReturnValue, returnValue, "Program output: $output")
                assertEquals(expectedOutput, output)
            }
        }
}
