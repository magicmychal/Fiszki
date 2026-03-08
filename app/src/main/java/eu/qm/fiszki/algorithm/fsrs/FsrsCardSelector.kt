package eu.qm.fiszki.algorithm.fsrs

import eu.qm.fiszki.model.flashcard.Flashcard
import java.util.Date

/**
 * Selects the next flashcard for a learning session when FSRS is enabled.
 *
 * Within a session the selector balances introducing new cards with
 * revisiting cards that were answered during this session. It shuffles
 * new cards so the first pick is not deterministic.
 *
 * Avoids drawing the same card twice in a row.
 */
class FsrsCardSelector {

    private val scheduler = FsrsScheduler()
    private var lastDrawnId: Int = -1
    private val seenThisSession = mutableSetOf<Int>()
    private var drawsSinceLastNew = 0

    fun selectNext(pool: List<Flashcard>, now: Date = Date()): Flashcard {
        if (pool.size == 1) {
            lastDrawnId = pool[0].id
            seenThisSession.add(pool[0].id)
            return pool[0]
        }

        val candidates = pool.filter { it.id != lastDrawnId }

        // Partition by FSRS state
        val overdueReview = mutableListOf<Pair<Flashcard, Double>>()
        val dueLearning = mutableListOf<Flashcard>()
        val newUnseen = mutableListOf<Flashcard>()
        val newSeen = mutableListOf<Flashcard>()

        for (card in candidates) {
            when (FsrsState.entries[card.fsrsState]) {
                FsrsState.New -> {
                    if (card.id in seenThisSession) {
                        newSeen.add(card)
                    } else {
                        newUnseen.add(card)
                    }
                }
                FsrsState.Learning, FsrsState.Relearning -> dueLearning.add(card)
                FsrsState.Review -> {
                    val fsrsCard = card.toFsrsCard()
                    val r = scheduler.retrievability(fsrsCard, now)
                    overdueReview.add(card to r)
                }
            }
        }

        // Prioritize introducing unseen new cards, but interleave with
        // due/review cards every few draws to reinforce recent answers.
        val shouldShowNew = newUnseen.isNotEmpty() &&
            (dueLearning.isEmpty() && overdueReview.isEmpty() || drawsSinceLastNew >= 2)

        if (shouldShowNew) {
            val selected = newUnseen.random()
            lastDrawnId = selected.id
            seenThisSession.add(selected.id)
            drawsSinceLastNew = 0
            return selected
        }

        // 1. Overdue review cards — lowest retrievability first
        if (overdueReview.isNotEmpty()) {
            overdueReview.sortBy { it.second }
            val selected = overdueReview[0].first
            lastDrawnId = selected.id
            seenThisSession.add(selected.id)
            drawsSinceLastNew++
            return selected
        }

        // 2. Due learning/relearning cards
        if (dueLearning.isNotEmpty()) {
            val selected = dueLearning.random()
            lastDrawnId = selected.id
            seenThisSession.add(selected.id)
            drawsSinceLastNew++
            return selected
        }

        // 3. Unseen new cards (if we skipped above due to interleaving)
        if (newUnseen.isNotEmpty()) {
            val selected = newUnseen.random()
            lastDrawnId = selected.id
            seenThisSession.add(selected.id)
            drawsSinceLastNew = 0
            return selected
        }

        // 4. Already-seen new cards (fallback when all new cards were seen)
        if (newSeen.isNotEmpty()) {
            val selected = newSeen.random()
            lastDrawnId = selected.id
            drawsSinceLastNew = 0
            return selected
        }

        // Fallback
        val selected = pool.random()
        lastDrawnId = selected.id
        return selected
    }
}
