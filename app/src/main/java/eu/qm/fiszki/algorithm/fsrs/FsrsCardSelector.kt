package eu.qm.fiszki.algorithm.fsrs

import eu.qm.fiszki.model.flashcard.Flashcard
import java.util.Date

/**
 * Selects the next flashcard for a learning session when FSRS is enabled.
 *
 * Priority order:
 * 1. Overdue review cards (lowest retrievability first)
 * 2. Due learning/relearning cards
 * 3. New cards
 *
 * Avoids drawing the same card twice in a row.
 */
class FsrsCardSelector {

    private val scheduler = FsrsScheduler()
    private var lastDrawnId: Int = -1

    fun selectNext(pool: List<Flashcard>, now: Date = Date()): Flashcard {
        if (pool.size == 1) {
            lastDrawnId = pool[0].id
            return pool[0]
        }

        val candidates = if (pool.size > 1) pool.filter { it.id != lastDrawnId } else pool

        // Partition by FSRS state
        val overdueReview = mutableListOf<Pair<Flashcard, Double>>()
        val dueLearning = mutableListOf<Flashcard>()
        val newCards = mutableListOf<Flashcard>()

        for (card in candidates) {
            when (FsrsState.entries[card.fsrsState]) {
                FsrsState.New -> newCards.add(card)
                FsrsState.Learning, FsrsState.Relearning -> dueLearning.add(card)
                FsrsState.Review -> {
                    val fsrsCard = card.toFsrsCard()
                    val r = scheduler.retrievability(fsrsCard, now)
                    // Card is "due" when retrievability drops below 90%
                    if (r < 0.9) {
                        overdueReview.add(card to r)
                    } else {
                        // Not yet due but still in pool — treat as low priority
                        overdueReview.add(card to r)
                    }
                }
            }
        }

        // 1. Overdue review cards — lowest retrievability first
        if (overdueReview.isNotEmpty()) {
            overdueReview.sortBy { it.second }
            val selected = overdueReview[0].first
            lastDrawnId = selected.id
            return selected
        }

        // 2. Due learning/relearning cards
        if (dueLearning.isNotEmpty()) {
            val selected = dueLearning[0]
            lastDrawnId = selected.id
            return selected
        }

        // 3. New cards
        if (newCards.isNotEmpty()) {
            val selected = newCards[0]
            lastDrawnId = selected.id
            return selected
        }

        // Fallback: shouldn't happen, but return random from pool
        val selected = pool.random()
        lastDrawnId = selected.id
        return selected
    }
}
