package click.quickclicker.fiszki

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckerTest {

    private lateinit var tchecker: Checker

    @Before
    fun setUp() {
        tchecker = Checker()
    }

    @Test
    fun testCheckTrue() {
        assertTrue(tchecker.check("test", "test"))
    }

    @Test
    fun testCheckFalse() {
        assertFalse(tchecker.check("test", "notest"))
    }

    @Test
    fun testStrictModeExactMatch() {
        assertTrue(tchecker.check("der Vorname", "der Vorname", strictMode = true))
    }

    @Test
    fun testStrictModeRejectsCaseInsensitive() {
        assertFalse(tchecker.check("der Vorname", "der vorname", strictMode = true))
    }

    @Test
    fun testStrictModeRejectsTrailingPeriod() {
        assertFalse(tchecker.check("hello", "hello.", strictMode = true))
    }

    @Test
    fun testNonStrictModeCaseInsensitive() {
        assertTrue(tchecker.check("der Vorname", "der vorname", strictMode = false))
    }

    @Test
    fun testNonStrictModeIgnoresTrailingPeriod() {
        assertTrue(tchecker.check("hello.", "hello", strictMode = false))
    }

    @Test
    fun testNonStrictModeBothTrailingPeriods() {
        assertTrue(tchecker.check("hello.", "hello.", strictMode = false))
    }

    @Test
    fun testNonStrictModeExactMatchStillWorks() {
        assertTrue(tchecker.check("test", "test", strictMode = false))
    }

    @Test
    fun testNonStrictModeWrongAnswer() {
        assertFalse(tchecker.check("hello", "world", strictMode = false))
    }
}
