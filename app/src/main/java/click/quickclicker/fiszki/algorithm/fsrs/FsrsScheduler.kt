package click.quickclicker.fiszki.algorithm.fsrs

import java.util.Date
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

/**
 * FSRS v6 scheduler. Stateless — call [schedule] with the current card state and rating
 * to get the updated card.
 *
 * Ported from the open-spaced-repetition FSRS-rs reference implementation.
 */
class FsrsScheduler(
    private val w: DoubleArray = FsrsParams.w,
    private val desiredRetention: Double = 0.9
) {

    private val decay: Double get() = -w[20]
    private val factor: Double get() = exp(ln(0.9) / decay) - 1.0

    fun schedule(card: FsrsCard, rating: FsrsRating, now: Date = Date()): FsrsCard {
        val nowMillis = now.time
        val elapsedDays = if (card.lastReview > 0) {
            ((nowMillis - card.lastReview) / DAY_MS).toInt().coerceAtLeast(0)
        } else {
            0
        }

        val updatedCard = card.copy(elapsedDays = elapsedDays)

        return when (updatedCard.state) {
            FsrsState.New -> scheduleNew(updatedCard, rating, nowMillis)
            FsrsState.Learning, FsrsState.Relearning -> scheduleShortTerm(updatedCard, rating, nowMillis)
            FsrsState.Review -> scheduleReview(updatedCard, rating, nowMillis)
        }
    }

    /**
     * Forgetting curve — returns probability of recall given elapsed time.
     */
    fun retrievability(card: FsrsCard, now: Date = Date()): Double {
        if (card.state == FsrsState.New) return 0.0
        if (card.stability <= 0.0) return 0.0
        val elapsedDays = if (card.lastReview > 0) {
            ((now.time - card.lastReview).toDouble() / DAY_MS).coerceAtLeast(0.0)
        } else {
            0.0
        }
        return forgettingCurve(elapsedDays, card.stability)
    }

    // --- New cards ---

    private fun scheduleNew(card: FsrsCard, rating: FsrsRating, nowMillis: Long): FsrsCard {
        val s = initStability(rating)
        val d = initDifficulty(rating)

        return if (rating == FsrsRating.Again || rating == FsrsRating.Hard) {
            card.copy(
                stability = s,
                difficulty = d,
                scheduledDays = 0,
                reps = card.reps + 1,
                state = FsrsState.Learning,
                lastReview = nowMillis
            )
        } else {
            val interval = nextInterval(s)
            card.copy(
                stability = s,
                difficulty = d,
                scheduledDays = interval,
                reps = card.reps + 1,
                state = FsrsState.Review,
                lastReview = nowMillis
            )
        }
    }

    // --- Learning / Relearning (short-term) ---

    private fun scheduleShortTerm(card: FsrsCard, rating: FsrsRating, nowMillis: Long): FsrsCard {
        val d = nextDifficulty(card.difficulty, rating)
        val s = shortTermStability(card.stability, rating)

        return if (rating == FsrsRating.Again || rating == FsrsRating.Hard) {
            val lapses = if (rating == FsrsRating.Again) card.lapses + 1 else card.lapses
            card.copy(
                stability = s,
                difficulty = d,
                scheduledDays = 0,
                reps = card.reps + 1,
                lapses = lapses,
                state = card.state,
                lastReview = nowMillis
            )
        } else {
            val interval = nextInterval(s)
            card.copy(
                stability = s,
                difficulty = d,
                scheduledDays = interval,
                reps = card.reps + 1,
                state = FsrsState.Review,
                lastReview = nowMillis
            )
        }
    }

    // --- Review cards ---

    private fun scheduleReview(card: FsrsCard, rating: FsrsRating, nowMillis: Long): FsrsCard {
        val r = forgettingCurve(card.elapsedDays.toDouble(), card.stability)
        val d = nextDifficulty(card.difficulty, rating)

        return if (rating == FsrsRating.Again) {
            val s = nextForgetStability(card.difficulty, card.stability, r)
            card.copy(
                stability = s,
                difficulty = d,
                scheduledDays = 0,
                reps = card.reps + 1,
                lapses = card.lapses + 1,
                state = FsrsState.Relearning,
                lastReview = nowMillis
            )
        } else {
            val s = nextRecallStability(card.difficulty, card.stability, r, rating)
            val interval = nextInterval(s)
            card.copy(
                stability = s,
                difficulty = d,
                scheduledDays = interval,
                reps = card.reps + 1,
                state = FsrsState.Review,
                lastReview = nowMillis
            )
        }
    }

    // --- Core formulas (FSRS v6) ---

    internal fun initStability(rating: FsrsRating): Double =
        w[rating.value - 1].coerceAtLeast(0.1)

    internal fun initDifficulty(rating: FsrsRating): Double {
        // w[4] - exp(w[5] * (rating - 1)) + 1
        val d = w[4] - exp(w[5] * (rating.value - 1)) + 1.0
        return clampDifficulty(d)
    }

    internal fun nextDifficulty(d: Double, rating: FsrsRating): Double {
        val deltaD = -w[6] * (rating.value - 3)
        val linearDamped = deltaD * (10.0 - d) / 9.0
        val newD = d + linearDamped
        return clampDifficulty(meanReversion(newD))
    }

    internal fun nextRecallStability(d: Double, s: Double, r: Double, rating: FsrsRating): Double {
        val hardPenalty = if (rating == FsrsRating.Hard) w[15] else 1.0
        val easyBonus = if (rating == FsrsRating.Easy) w[16] else 1.0
        return s * (exp(w[8]) *
            (11.0 - d) *
            s.pow(-w[9]) *
            (exp((1.0 - r) * w[10]) - 1.0) *
            hardPenalty *
            easyBonus + 1.0)
    }

    internal fun nextForgetStability(d: Double, s: Double, r: Double): Double {
        val base = w[11] *
            d.pow(-w[12]) *
            ((s + 1.0).pow(w[13]) - 1.0) *
            exp((1.0 - r) * w[14])
        val minStability = s / exp(w[17] * w[18])
        return max(base, minStability).coerceAtLeast(0.1)
    }

    private fun shortTermStability(s: Double, rating: FsrsRating): Double {
        return s * max(1.0,
            exp(w[17] * (rating.value - 3 + w[18])) * s.pow(-w[19])
        )
    }

    private fun forgettingCurve(elapsedDays: Double, stability: Double): Double {
        if (stability <= 0.0) return 0.0
        return (elapsedDays / stability * factor + 1.0).pow(decay)
    }

    internal fun nextInterval(s: Double): Int {
        val interval = round(
            s / factor * (desiredRetention.pow(1.0 / decay) - 1.0)
        ).toInt()
        return interval.coerceIn(1, 36500)
    }

    private fun meanReversion(newD: Double): Double {
        // w[7] * (initDifficulty(Easy) - newD) + newD
        val initEasy = w[4] - exp(w[5] * (4 - 1)) + 1.0
        return w[7] * (initEasy - newD) + newD
    }

    private fun clampDifficulty(d: Double): Double = min(10.0, max(1.0, d))

    companion object {
        const val DAY_MS = 86_400_000L
    }
}
