package click.quickclicker.fiszki.algorithm.fsrs

/**
 * Derives an [FsrsRating] from user behaviour during a learning session.
 *
 * | Rating | Condition |
 * |--------|-----------|
 * | Easy   | Correct on 1st attempt, within 2 minutes |
 * | Good   | Correct on 1st attempt but >2 min, OR answer within 2-char edit distance |
 * | Hard   | Correct after multiple attempts |
 * | Again  | User clicks "Skip" |
 */
object FsrsRatingMapper {

    private const val FAST_THRESHOLD_MS = 2 * 60 * 1000L // 2 minutes
    private const val CLOSE_EDIT_DISTANCE = 2

    fun mapToRating(
        wasSkipped: Boolean,
        attemptCount: Int,
        isCorrect: Boolean,
        elapsedTimeMs: Long,
        editDistance: Int
    ): FsrsRating {
        if (wasSkipped) return FsrsRating.Again

        if (!isCorrect) return FsrsRating.Again

        // Correct after multiple attempts
        if (attemptCount > 1) return FsrsRating.Hard

        // First attempt correct
        if (elapsedTimeMs <= FAST_THRESHOLD_MS && editDistance == 0) {
            return FsrsRating.Easy
        }

        return FsrsRating.Good
    }
}
