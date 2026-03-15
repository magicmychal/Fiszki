package click.quickclicker.fiszki.model.flashcard

import android.content.Context
import click.quickclicker.fiszki.database.FiszkiDatabase

class FlashcardRepository(context: Context) {

    private val dao = FiszkiDatabase.getInstance(context).flashcardDao()
    var flashcardList: ArrayList<Flashcard> = arrayListOf()

    fun getAllFlashcards(): ArrayList<Flashcard> {
        flashcardList = ArrayList(dao.getAll())
        return flashcardList
    }

    fun countFlashcards(): Int = dao.count()

    fun addFlashcard(flashcard: Flashcard) {
        dao.insert(flashcard)
    }

    fun addFlashcards(arrayListFlashcards: ArrayList<Flashcard>) {
        dao.insertAll(arrayListFlashcards)
    }

    fun getFlashcardByName(name: String): Flashcard? {
        val result = dao.getByWord(name)
        if (result != null) {
            flashcardList = arrayListOf(result)
        } else {
            flashcardList = arrayListOf()
        }
        return result
    }

    fun isFirst(): Boolean = getAllFlashcards().size == 1

    fun deleteFlashcard(flashcard: Flashcard) {
        dao.delete(flashcard)
    }

    fun deleteFlashcards(flashcards: ArrayList<Flashcard>) {
        for (flashcard in flashcards) {
            dao.delete(flashcard)
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        dao.update(flashcard)
    }

    fun getFlashcardsByPriority(priority: Int): ArrayList<Flashcard> {
        return ArrayList(dao.getByPriority(priority))
    }

    fun getRandomFlashcardByPririty(priority: Int): Flashcard {
        val flashcards = getFlashcardsByPriority(priority)
        return flashcards.random()
    }

    fun getFlashcardsByCategoryID(categoryID: Int): ArrayList<Flashcard> {
        return ArrayList(dao.getByCategoryID(categoryID))
    }

    fun upFlashcardFailStatistic(flashcard: Flashcard) {
        flashcard.upStaticFail()
        dao.update(flashcard)
    }

    fun upFlashcardPassStatistic(flashcard: Flashcard) {
        flashcard.upStaticPass()
        dao.update(flashcard)
    }

    fun upFlashcardPriority(flashcard: Flashcard) {
        flashcard.upPriority()
        dao.update(flashcard)
    }

    fun downFlashcardPriority(flashcard: Flashcard) {
        flashcard.downPriority()
        dao.update(flashcard)
    }

    fun resetFlashcardStatistic(flashcard: Flashcard) {
        flashcard.resetStatictic()
        dao.update(flashcard)
    }

    fun updateFsrsState(flashcard: Flashcard) {
        dao.update(flashcard)
    }
}
