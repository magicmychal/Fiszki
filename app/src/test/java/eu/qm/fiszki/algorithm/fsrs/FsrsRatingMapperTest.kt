package eu.qm.fiszki.algorithm.fsrs

import org.junit.Assert.assertEquals
import org.junit.Test

class FsrsRatingMapperTest {

    @Test
    fun skipped_returnsAgain() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = true,
            attemptCount = 0,
            isCorrect = false,
            elapsedTimeMs = 0,
            editDistance = 0
        )
        assertEquals(FsrsRating.Again, rating)
    }

    @Test
    fun incorrect_returnsAgain() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 1,
            isCorrect = false,
            elapsedTimeMs = 5000,
            editDistance = 5
        )
        assertEquals(FsrsRating.Again, rating)
    }

    @Test
    fun correctFirstAttemptFast_returnsEasy() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 1,
            isCorrect = true,
            elapsedTimeMs = 10_000, // 10 seconds
            editDistance = 0
        )
        assertEquals(FsrsRating.Easy, rating)
    }

    @Test
    fun correctFirstAttemptSlow_returnsGood() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 1,
            isCorrect = true,
            elapsedTimeMs = 150_000, // 2.5 minutes
            editDistance = 0
        )
        assertEquals(FsrsRating.Good, rating)
    }

    @Test
    fun correctFirstAttemptCloseEditDistance_returnsGood() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 1,
            isCorrect = true,
            elapsedTimeMs = 10_000, // fast
            editDistance = 2 // close but not exact
        )
        assertEquals(FsrsRating.Good, rating)
    }

    @Test
    fun correctMultipleAttempts_returnsHard() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 3,
            isCorrect = true,
            elapsedTimeMs = 5000,
            editDistance = 0
        )
        assertEquals(FsrsRating.Hard, rating)
    }

    @Test
    fun correctSecondAttempt_returnsHard() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 2,
            isCorrect = true,
            elapsedTimeMs = 5000,
            editDistance = 0
        )
        assertEquals(FsrsRating.Hard, rating)
    }

    @Test
    fun exactlyAtTwoMinutes_returnsEasy() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 1,
            isCorrect = true,
            elapsedTimeMs = 120_000, // exactly 2 minutes
            editDistance = 0
        )
        assertEquals(FsrsRating.Easy, rating)
    }

    @Test
    fun justOverTwoMinutes_returnsGood() {
        val rating = FsrsRatingMapper.mapToRating(
            wasSkipped = false,
            attemptCount = 1,
            isCorrect = true,
            elapsedTimeMs = 120_001, // just over 2 minutes
            editDistance = 0
        )
        assertEquals(FsrsRating.Good, rating)
    }
}
