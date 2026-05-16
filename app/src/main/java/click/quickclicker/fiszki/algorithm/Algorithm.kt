package click.quickclicker.fiszki.algorithm

import android.content.Context
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class Algorithm(context: Context) {

    private val flashcardRepository = FlashcardRepository(context)
    private val catcherFlashcardToAlgorithm = CatcherFlashcardToAlgorithm(context)
    private val drawer = Drawer()
    private var lastDrawnFlashcard: Flashcard? = null

    fun drawCardAlgorithm(flashcardPool: ArrayList<Flashcard>): Flashcard {
        if (flashcardPool.isEmpty()) {
            throw IllegalArgumentException("Flashcard pool cannot be empty")
        }

        if (flashcardPool.size == 1) {
            lastDrawnFlashcard = flashcardPool[0]
            return flashcardPool[0]
        }

        val priorityCount = PriorityCount(flashcardPool).priorityCount() ?: return flashcardPool.random()
        val multiplierPoints = MultiplierPoints(priorityCount).multipler()

        val maxRange = multiplierPoints[4]
        if (maxRange <= 0) return drawRandomAvoidingLast(flashcardPool)

        // Pre-group flashcards by priority to avoid O(N) filtering inside the loop
        // Priority 0 is treated as priority 1 as per existing logic
        val groupedCards = flashcardPool.groupBy { if (it.priority == 0) 1 else it.priority }

        var drawnCard: Flashcard? = null
        var attempts = 0
        val maxAttempts = 10

        while (attempts < maxAttempts) {
            val randomPoint = drawer.drawInteger(maxRange)
            val selectedPriority = when {
                randomPoint < multiplierPoints[0] -> 1
                randomPoint < multiplierPoints[1] -> 2
                randomPoint < multiplierPoints[2] -> 3
                randomPoint < multiplierPoints[3] -> 4
                else -> 5
            }

            val cardsWithPriority = groupedCards[selectedPriority]
            if (cardsWithPriority != null && cardsWithPriority.isNotEmpty()) {
                val candidate = cardsWithPriority.random()
                if (candidate.id != lastDrawnFlashcard?.id || flashcardPool.size <= 1) {
                    drawnCard = candidate
                    break
                }
            }
            attempts++
        }

        val finalCard = drawnCard ?: drawRandomAvoidingLast(flashcardPool)
        lastDrawnFlashcard = finalCard
        return finalCard
    }

    private fun drawRandomAvoidingLast(pool: ArrayList<Flashcard>): Flashcard {
        var candidate = pool.random()
        if (pool.size > 1 && candidate.id == lastDrawnFlashcard?.id) {
            val remaining = pool.filter { it.id != lastDrawnFlashcard?.id }
            if (remaining.isNotEmpty()) {
                candidate = remaining.random()
            }
        }
        return candidate
    }
}
