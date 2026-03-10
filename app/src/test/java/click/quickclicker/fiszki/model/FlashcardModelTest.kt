package click.quickclicker.fiszki.model

import click.quickclicker.fiszki.algorithm.fsrs.FsrsCard
import click.quickclicker.fiszki.algorithm.fsrs.FsrsState
import click.quickclicker.fiszki.model.flashcard.Flashcard
import org.junit.Assert.assertEquals
import org.junit.Test

class FlashcardModelTest {

    @Test
    fun setWord_getWord_apostropheRoundtrip() {
        val card = Flashcard()
        card.setWord("don't")
        assertEquals("don't", card.getWord())
    }

    @Test
    fun setTranslation_getTranslation_apostropheRoundtrip() {
        val card = Flashcard()
        card.setTranslation("it's")
        assertEquals("it's", card.getTranslation())
    }

    @Test
    fun wordDB_returnsEncodedForm() {
        val card = Flashcard()
        card.setWord("don't stop")
        assertEquals("don%sq%t stop", card.wordDB)
    }

    @Test
    fun translationDB_returnsEncodedForm() {
        val card = Flashcard()
        card.setTranslation("it's here")
        assertEquals("it%sq%s here", card.translationDB)
    }

    @Test
    fun getWord_nullWord_returnsEmptyString() {
        val card = Flashcard()
        assertEquals("", card.getWord())
    }

    @Test
    fun getTranslation_nullTranslation_returnsEmptyString() {
        val card = Flashcard()
        assertEquals("", card.getTranslation())
    }

    @Test
    fun wordDB_nullWord_returnsEmptyString() {
        val card = Flashcard()
        assertEquals("", card.wordDB)
    }

    @Test
    fun upPriority_clampsAtSix() {
        val card = Flashcard()
        // When priority is 5, upPriority increments to 6
        card.priority = 5
        card.upPriority()
        assertEquals(6, card.priority)
        // When priority is 6 (> 5), upPriority clamps back to 5
        card.upPriority()
        assertEquals(5, card.priority)
    }

    @Test
    fun upPriority_incrementsNormally() {
        val card = Flashcard()
        card.priority = 3
        card.upPriority()
        assertEquals(4, card.priority)
    }

    @Test
    fun downPriority_clampsAtZero() {
        val card = Flashcard()
        card.priority = 0
        card.downPriority()
        assertEquals(0, card.priority)
    }

    @Test
    fun downPriority_decrementsNormally() {
        val card = Flashcard()
        card.priority = 3
        card.downPriority()
        assertEquals(2, card.priority)
    }

    @Test
    fun upStaticFail_increments() {
        val card = Flashcard()
        assertEquals(0, card.staticFail)
        card.upStaticFail()
        assertEquals(1, card.staticFail)
        card.upStaticFail()
        assertEquals(2, card.staticFail)
    }

    @Test
    fun upStaticPass_increments() {
        val card = Flashcard()
        assertEquals(0, card.staticPass)
        card.upStaticPass()
        assertEquals(1, card.staticPass)
    }

    @Test
    fun resetStatictic_zeroesBoth() {
        val card = Flashcard()
        card.upStaticFail()
        card.upStaticFail()
        card.upStaticPass()
        card.resetStatictic()
        assertEquals(0, card.staticFail)
        assertEquals(0, card.staticPass)
    }

    @Test
    fun toFsrsCard_applyFsrsCard_roundtrip() {
        val card = Flashcard()
        card.fsrsStability = 4.5
        card.fsrsDifficulty = 6.7
        card.fsrsElapsedDays = 10
        card.fsrsScheduledDays = 15
        card.fsrsReps = 8
        card.fsrsLapses = 2
        card.fsrsState = FsrsState.Review.ordinal
        card.fsrsLastReview = 1700000000000L

        val fsrsCard = card.toFsrsCard()
        assertEquals(4.5, fsrsCard.stability, 0.001)
        assertEquals(6.7, fsrsCard.difficulty, 0.001)
        assertEquals(10, fsrsCard.elapsedDays)
        assertEquals(15, fsrsCard.scheduledDays)
        assertEquals(8, fsrsCard.reps)
        assertEquals(2, fsrsCard.lapses)
        assertEquals(FsrsState.Review, fsrsCard.state)
        assertEquals(1700000000000L, fsrsCard.lastReview)

        // Apply back to a fresh card
        val card2 = Flashcard()
        card2.applyFsrsCard(fsrsCard)
        assertEquals(card.fsrsStability, card2.fsrsStability, 0.001)
        assertEquals(card.fsrsDifficulty, card2.fsrsDifficulty, 0.001)
        assertEquals(card.fsrsElapsedDays, card2.fsrsElapsedDays)
        assertEquals(card.fsrsScheduledDays, card2.fsrsScheduledDays)
        assertEquals(card.fsrsReps, card2.fsrsReps)
        assertEquals(card.fsrsLapses, card2.fsrsLapses)
        assertEquals(card.fsrsState, card2.fsrsState)
        assertEquals(card.fsrsLastReview, card2.fsrsLastReview)
    }

    @Test
    fun toFsrsCard_mapsStateEnumCorrectly() {
        for (state in FsrsState.entries) {
            val card = Flashcard()
            card.fsrsState = state.ordinal
            assertEquals(state, card.toFsrsCard().state)
        }
    }

    @Test
    fun multipleApostrophes_encodedCorrectly() {
        val card = Flashcard()
        card.setWord("it's a don't-know-why")
        assertEquals("it's a don't-know-why", card.getWord())
        assertEquals("it%sq%s a don%sq%t-know-why", card.wordDB)
    }
}
