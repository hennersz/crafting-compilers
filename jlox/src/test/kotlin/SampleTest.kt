import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SampleTest {

    private val testSample: Sample = Sample()

    @Test
    fun sum() {
        val expected = 42
        assertEquals(expected, testSample.sum(40, 2))
    }
}