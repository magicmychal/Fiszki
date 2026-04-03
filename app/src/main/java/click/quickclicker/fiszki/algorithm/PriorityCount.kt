package click.quickclicker.fiszki.algorithm

import click.quickclicker.fiszki.model.flashcard.Flashcard

class PriorityCount(private val flashcards: ArrayList<Flashcard>) {

    private val priorityCounts = IntArray(5)
    val groupedCards = Array(5) { mutableListOf<Flashcard>() }

    fun priorityCount(): IntArray? {
        if (flashcards.isEmpty()) return null
        for (flashcard in flashcards) {
            val p = when (flashcard.priority) {
                0, 1 -> 1
                2 -> 2
                3 -> 3
                4 -> 4
                else -> 5
            }
            priorityCounts[p - 1]++
            groupedCards[p - 1].add(flashcard)
        }
        return priorityCounts
    }
}
