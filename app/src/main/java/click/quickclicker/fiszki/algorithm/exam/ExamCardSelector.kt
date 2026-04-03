package click.quickclicker.fiszki.algorithm.exam

import click.quickclicker.fiszki.model.flashcard.Flashcard

/**
 * Selects flashcards for an exam session without repetition.
 *
 * The pool is shuffled once at creation. Cards are drawn sequentially
 * via [selectNext]. Once all cards have been shown, [selectNext] returns null.
 * No card is ever repeated within a single exam session.
 */
class ExamCardSelector(pool: List<Flashcard>) {

    private val queue = ArrayDeque(pool.shuffled())

    /**
     * Returns the next unshown flashcard, or null if all cards have been shown.
     */
    fun selectNext(): Flashcard? = if (queue.isEmpty()) null else queue.removeFirst()

    /**
     * Number of cards remaining in the queue.
     */
    fun remaining(): Int = queue.size
}

