package eu.qm.fiszki

import eu.qm.fiszki.model.flashcard.Flashcard
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AlgorithmTest {

    private lateinit var fiszka: Flashcard

    @Before
    fun setUp() {
        fiszka = Flashcard()
    }

    @Test
    fun testDrawCardAlgorithm() {
        assertNotNull(fiszka)
    }
}
