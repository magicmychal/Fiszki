package eu.qm.fiszki.algorithm.fsrs

import eu.qm.fiszki.model.flashcard.Flashcard
import java.util.Date

/**
 * Selects the next flashcard for a learning session when FSRS is enabled.
 *
 * On first call the pool is shuffled into a queue. Cards are drawn in order.
 * Cards that were answered incorrectly are re-inserted a few positions ahead
 * so they come back soon but not immediately.
 *
 * Avoids drawing the same card twice in a row.
 */
class FsrsCardSelector {

    private val queue = ArrayDeque<Flashcard>()
    private var lastDrawnId: Int = -1
    private var initialized = false

    fun selectNext(pool: List<Flashcard>, now: Date = Date()): Flashcard {
        if (!initialized) {
            queue.addAll(pool.shuffled())
            initialized = true
        }

        // If queue is empty, reshuffle the full pool (all cards seen once)
        if (queue.isEmpty()) {
            queue.addAll(pool.shuffled())
        }

        // Avoid same card twice in a row
        if (queue.size > 1 && queue.first().id == lastDrawnId) {
            val skipped = queue.removeFirst()
            queue.addLast(skipped)
        }

        val selected = queue.removeFirst()
        lastDrawnId = selected.id
        return selected
    }

    /**
     * Re-insert a card that was answered incorrectly so it appears again
     * after a short gap (2-4 cards later).
     */
    fun reinsertForRetry(flashcard: Flashcard) {
        val insertPos = minOf(RETRY_GAP, queue.size)
        // Convert to mutable list for indexed insert, then rebuild deque
        val list = queue.toMutableList()
        list.add(insertPos, flashcard)
        queue.clear()
        queue.addAll(list)
    }

    companion object {
        private const val RETRY_GAP = 3
    }
}
