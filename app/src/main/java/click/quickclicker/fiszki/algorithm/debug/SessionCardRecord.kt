package click.quickclicker.fiszki.algorithm.debug

import click.quickclicker.fiszki.algorithm.fsrs.FsrsRating
import click.quickclicker.fiszki.algorithm.fsrs.FsrsState

data class SessionCardRecord(
    val word: String,
    val translation: String,
    val rating: FsrsRating?,
    val attemptCount: Int,
    val elapsedTimeMs: Long,
    val wasSkipped: Boolean,
    val wasCorrect: Boolean,
    val stabilityBefore: Double,
    val stabilityAfter: Double,
    val difficultyBefore: Double,
    val difficultyAfter: Double,
    val retrievability: Double,
    val scheduledDays: Int,
    val fsrsState: FsrsState,
    val reps: Int,
    val lapses: Int,
    val priority: Int
)
