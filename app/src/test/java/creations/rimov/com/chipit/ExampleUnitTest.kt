package creations.rimov.com.chipit

import android.util.Log
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val inone = 3
        val intwo = 2

        var match = false

        val ONE = 1
        val TWO = 2

        when(inone or intwo) {
            ONE, TWO -> match = true
        }

        assertEquals(true, match)
    }
}
