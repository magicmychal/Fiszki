package click.quickclicker.fiszki.algorithm

import click.quickclicker.fiszki.model.flashcard.Flashcard

class PriorityCount(private val flashcards: ArrayList<Flashcard>) {

    private val priority = IntArray(5)

    fun priorityCount(): IntArray? {
        if (flashcards.isEmpty()) return null
        for (flashcard in flashcards) {
            when (flashcard.priority) {
                1 -> priority[0]++
                2 -> priority[1]++
                3 -> priority[2]++
                4 -> priority[3]++
                5 -> priority[4]++
            }
        }
        return priority
    }
}
