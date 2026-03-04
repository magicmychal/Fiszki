package eu.qm.fiszki

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
}
