package eu.qm.fiszki

import org.junit.Assert.assertEquals
import org.junit.Test

class CheckerEditDistanceTest {

    @Test
    fun identicalStrings_returnsZero() {
        assertEquals(0, Checker.editDistance("hello", "hello"))
    }

    @Test
    fun emptyAndNonEmpty_returnsLength() {
        assertEquals(5, Checker.editDistance("", "hello"))
        assertEquals(5, Checker.editDistance("hello", ""))
    }

    @Test
    fun bothEmpty_returnsZero() {
        assertEquals(0, Checker.editDistance("", ""))
    }

    @Test
    fun singleInsertion() {
        assertEquals(1, Checker.editDistance("cat", "cats"))
    }

    @Test
    fun singleDeletion() {
        assertEquals(1, Checker.editDistance("cats", "cat"))
    }

    @Test
    fun singleSubstitution() {
        assertEquals(1, Checker.editDistance("cat", "bat"))
    }

    @Test
    fun multipleEdits() {
        assertEquals(3, Checker.editDistance("kitten", "sitting"))
    }

    @Test
    fun completelyDifferent() {
        assertEquals(3, Checker.editDistance("abc", "xyz"))
    }

    @Test
    fun caseMatters() {
        assertEquals(1, Checker.editDistance("Hello", "hello"))
    }

    @Test
    fun twoCharDistance() {
        assertEquals(2, Checker.editDistance("helo", "hello!"))
    }

    @Test
    fun unicodeCharacters() {
        assertEquals(1, Checker.editDistance("cafe", "café"))
    }
}
