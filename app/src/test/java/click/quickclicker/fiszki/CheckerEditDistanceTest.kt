package click.quickclicker.fiszki

import org.junit.Assert.assertArrayEquals
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

    // --- alignDiffs tests ---

    @Test
    fun alignDiffs_insertion_onlyExtraCharMarked() {
        // User typed "Hausemeisterin", correct is "Hausmeisterin"
        // Only the extra "e" at index 4 should be marked in user answer
        val (userDiffs, correctDiffs) = Checker.alignDiffs("Hausemeisterin", "Hausmeisterin")
        val expectedUser = booleanArrayOf(
            false, false, false, false, // H a u s
            true,                        // e (extra)
            false, false, false, false, false, false, false, false, false // meisterin
        )
        assertArrayEquals(expectedUser, userDiffs)
        // Correct answer has no diffs — nothing is "wrong" in it
        assertArrayEquals(BooleanArray(13) { false }, correctDiffs)
    }

    @Test
    fun alignDiffs_substitution_onlyChangedCharMarked() {
        // User typed "bat", correct is "cat" — only index 0 differs
        val (userDiffs, correctDiffs) = Checker.alignDiffs("bat", "cat")
        assertArrayEquals(booleanArrayOf(true, false, false), userDiffs)
        assertArrayEquals(booleanArrayOf(true, false, false), correctDiffs)
    }

    @Test
    fun alignDiffs_deletion_missingCharMarkedInCorrect() {
        // User typed "helo", correct is "hello" — user is missing one 'l'
        val (userDiffs, correctDiffs) = Checker.alignDiffs("helo", "hello")
        // User's chars are all used in the alignment, only the missing 'l' shows in correct
        val userHasOneDiff = userDiffs.count { it }
        val correctHasOneDiff = correctDiffs.count { it }
        assertEquals(0, userHasOneDiff)
        assertEquals(1, correctHasOneDiff)
    }

    @Test
    fun alignDiffs_identical_noDiffs() {
        val (userDiffs, correctDiffs) = Checker.alignDiffs("hello", "hello")
        assertArrayEquals(BooleanArray(5) { false }, userDiffs)
        assertArrayEquals(BooleanArray(5) { false }, correctDiffs)
    }

    @Test
    fun alignDiffs_completelyDifferent() {
        val (userDiffs, correctDiffs) = Checker.alignDiffs("abc", "xyz")
        assertArrayEquals(booleanArrayOf(true, true, true), userDiffs)
        assertArrayEquals(booleanArrayOf(true, true, true), correctDiffs)
    }
}
