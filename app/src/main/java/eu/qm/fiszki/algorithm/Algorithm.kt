package eu.qm.fiszki.algorithm

import android.content.Context
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class Algorithm(context: Context) {

    var draw: Int = 0
    private val flashcardRepository = FlashcardRepository(context)
    private val catcherFlashcardToAlgorithm = CatcherFlashcardToAlgorithm(context)

    fun drawCardAlgorithm(flashcardPool: ArrayList<Flashcard>): Flashcard {
        // TODO: Implement priority-based algorithm using PriorityCount, MultiplierPoints, Drawer
        // For now, uses random selection from the pool
        return flashcardPool.random()
    }
}
