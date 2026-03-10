package click.quickclicker.fiszki.algorithm

import android.content.Context
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class Algorithm(context: Context) {

    var draw: Int = 0
    private val flashcardRepository = FlashcardRepository(context)
    private val catcherFlashcardToAlgorithm = CatcherFlashcardToAlgorithm(context)
    private var lastDrawnFlashcard: Flashcard? = null

    fun drawCardAlgorithm(flashcardPool: ArrayList<Flashcard>): Flashcard {
        // TODO: Implement priority-based algorithm using PriorityCount, MultiplierPoints, Drawer
        // For now, uses random selection from the pool

        // If pool has only one card, return it (no choice)
        if (flashcardPool.size == 1) {
            lastDrawnFlashcard = flashcardPool[0]
            return flashcardPool[0]
        }

        // If pool has multiple cards, ensure we don't draw the same card twice in a row
        var drawnCard = flashcardPool.random()
        var attempts = 0
        val maxAttempts = 10

        // Try to draw a different card than the last one
        while (lastDrawnFlashcard != null &&
               drawnCard.id == lastDrawnFlashcard?.id &&
               attempts < maxAttempts) {
            drawnCard = flashcardPool.random()
            attempts++
        }

        lastDrawnFlashcard = drawnCard
        return drawnCard
    }
}
