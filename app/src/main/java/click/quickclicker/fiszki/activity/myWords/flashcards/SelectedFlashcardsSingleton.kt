package click.quickclicker.fiszki.activity.myWords.flashcards

import click.quickclicker.fiszki.model.flashcard.Flashcard

object SelectedFlashcardsSingleton {

    const val STATUS_ON = 1
    const val STATUS_OFF = 0

    private val mFlashcards = ArrayList<Flashcard>()

    @JvmStatic
    var status: Int = STATUS_OFF

    @JvmStatic
    fun getFlashcards(): ArrayList<Flashcard> = mFlashcards

    @JvmStatic
    fun addFlashcards(flashcard: Flashcard) {
        mFlashcards.add(flashcard)
    }

    @JvmStatic
    fun clearFlashcards() {
        mFlashcards.clear()
        setStatuOff()
    }

    @JvmStatic
    fun findFlashcard(flashcard: Flashcard): Boolean {
        return mFlashcards.any { it == flashcard }
    }

    @JvmStatic
    fun removeFlashcard(flashcard: Flashcard) {
        mFlashcards.remove(flashcard)
    }

    @JvmStatic
    fun setStatuOn() {
        status = STATUS_ON
    }

    @JvmStatic
    fun setStatuOff() {
        status = STATUS_OFF
    }

    @JvmStatic
    fun isFlashcard(flashcard: Flashcard): Boolean {
        return mFlashcards.any { it.id == flashcard.id }
    }
}
