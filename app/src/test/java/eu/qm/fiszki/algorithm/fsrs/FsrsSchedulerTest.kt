package eu.qm.fiszki.algorithm.fsrs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class FsrsSchedulerTest {

    private lateinit var scheduler: FsrsScheduler

    @Before
    fun setUp() {
        scheduler = FsrsScheduler()
    }

    // --- New card transitions ---

    @Test
    fun newCardGood_transitionsToReview() {
        val card = FsrsCard()
        val result = scheduler.schedule(card, FsrsRating.Good, Date())
        assertEquals(FsrsState.Review, result.state)
        assertTrue(result.stability > 0)
        assertTrue(result.difficulty > 0)
        assertTrue(result.scheduledDays >= 1)
        assertEquals(1, result.reps)
    }

    @Test
    fun newCardEasy_transitionsToReview() {
        val card = FsrsCard()
        val result = scheduler.schedule(card, FsrsRating.Easy, Date())
        assertEquals(FsrsState.Review, result.state)
        assertTrue(result.scheduledDays >= 1)
    }

    @Test
    fun newCardAgain_transitionsToLearning() {
        val card = FsrsCard()
        val result = scheduler.schedule(card, FsrsRating.Again, Date())
        assertEquals(FsrsState.Learning, result.state)
        assertEquals(0, result.scheduledDays)
        assertEquals(1, result.reps)
    }

    @Test
    fun newCardHard_transitionsToLearning() {
        val card = FsrsCard()
        val result = scheduler.schedule(card, FsrsRating.Hard, Date())
        assertEquals(FsrsState.Learning, result.state)
        assertEquals(0, result.scheduledDays)
    }

    // --- Stability growth ---

    @Test
    fun easyRating_givesHigherStabilityThanGood() {
        val card = FsrsCard()
        val easy = scheduler.schedule(card, FsrsRating.Easy, Date())
        val good = scheduler.schedule(card, FsrsRating.Good, Date())
        assertTrue(easy.stability > good.stability)
    }

    @Test
    fun goodRating_givesHigherStabilityThanHard() {
        val card = FsrsCard()
        val good = scheduler.schedule(card, FsrsRating.Good, Date())
        val hard = scheduler.schedule(card, FsrsRating.Hard, Date())
        assertTrue(good.stability > hard.stability)
    }

    // --- Review card: recall vs lapse ---

    @Test
    fun reviewCardGood_staysInReview() {
        val now = Date()
        val card = FsrsCard(
            stability = 5.0,
            difficulty = 5.0,
            state = FsrsState.Review,
            reps = 3,
            lastReview = now.time - 5 * FsrsScheduler.DAY_MS
        )
        val result = scheduler.schedule(card, FsrsRating.Good, now)
        assertEquals(FsrsState.Review, result.state)
        assertTrue(result.stability > card.stability)
        assertTrue(result.scheduledDays >= 1)
        assertEquals(4, result.reps)
    }

    @Test
    fun reviewCardAgain_transitionsToRelearning() {
        val now = Date()
        val card = FsrsCard(
            stability = 5.0,
            difficulty = 5.0,
            state = FsrsState.Review,
            reps = 3,
            lapses = 0,
            lastReview = now.time - 5 * FsrsScheduler.DAY_MS
        )
        val result = scheduler.schedule(card, FsrsRating.Again, now)
        assertEquals(FsrsState.Relearning, result.state)
        assertEquals(0, result.scheduledDays)
        assertEquals(1, result.lapses)
    }

    // --- Difficulty adjustment ---

    @Test
    fun easyRating_decreasesDifficulty() {
        val d = scheduler.initDifficulty(FsrsRating.Good)
        val newD = scheduler.nextDifficulty(d, FsrsRating.Easy)
        assertTrue(newD < d)
    }

    @Test
    fun againRating_increasesDifficulty() {
        val d = scheduler.initDifficulty(FsrsRating.Good)
        val newD = scheduler.nextDifficulty(d, FsrsRating.Again)
        assertTrue(newD > d)
    }

    @Test
    fun difficulty_clampedBetween1And10() {
        val veryLow = scheduler.nextDifficulty(1.0, FsrsRating.Easy)
        assertTrue(veryLow >= 1.0)
        val veryHigh = scheduler.nextDifficulty(10.0, FsrsRating.Again)
        assertTrue(veryHigh <= 10.0)
    }

    // --- Retrievability ---

    @Test
    fun retrievability_decreasesOverTime() {
        val now = Date()
        val card = FsrsCard(
            stability = 10.0,
            state = FsrsState.Review,
            lastReview = now.time - FsrsScheduler.DAY_MS
        )
        val r1 = scheduler.retrievability(card, now)

        val laterCard = card.copy(lastReview = now.time - 30 * FsrsScheduler.DAY_MS)
        val r30 = scheduler.retrievability(laterCard, now)

        assertTrue(r1 > r30)
    }

    @Test
    fun retrievability_newCard_isZero() {
        val card = FsrsCard()
        assertEquals(0.0, scheduler.retrievability(card), 0.001)
    }

    // --- Interval bounds ---

    @Test
    fun nextInterval_atLeast1Day() {
        val interval = scheduler.nextInterval(0.01)
        assertTrue(interval >= 1)
    }

    @Test
    fun nextInterval_atMost36500Days() {
        val interval = scheduler.nextInterval(1_000_000.0)
        assertTrue(interval <= 36500)
    }

    // --- Learning → Review promotion ---

    @Test
    fun learningCardGood_promotesToReview() {
        val card = FsrsCard(
            stability = 1.0,
            difficulty = 5.0,
            state = FsrsState.Learning,
            reps = 1,
            lastReview = System.currentTimeMillis()
        )
        val result = scheduler.schedule(card, FsrsRating.Good, Date())
        assertEquals(FsrsState.Review, result.state)
        assertTrue(result.scheduledDays >= 1)
    }

    @Test
    fun learningCardAgain_staysInLearning() {
        val card = FsrsCard(
            stability = 1.0,
            difficulty = 5.0,
            state = FsrsState.Learning,
            reps = 1,
            lastReview = System.currentTimeMillis()
        )
        val result = scheduler.schedule(card, FsrsRating.Again, Date())
        assertEquals(FsrsState.Learning, result.state)
        assertEquals(0, result.scheduledDays)
    }
}
