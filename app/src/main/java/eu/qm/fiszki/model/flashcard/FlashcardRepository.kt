package eu.qm.fiszki.model.flashcard

import android.content.Context
import com.j256.ormlite.android.apptools.OpenHelperManager
import eu.qm.fiszki.database.ORM.DBHelper

class FlashcardRepository(context: Context) {

    val dbHelper: DBHelper = OpenHelperManager.getHelper(context, DBHelper::class.java)
    val flashcardDao = dbHelper.getFlashcardDao()
    var flashcardList: ArrayList<Flashcard> = arrayListOf()
    private val qb = flashcardDao.queryBuilder()

    fun getAllFlashcards(): ArrayList<Flashcard> {
        flashcardList = ArrayList(flashcardDao.queryForAll())
        return flashcardList
    }

    fun countFlashcards(): Int = flashcardDao.countOf().toInt()

    fun addFlashcard(flashcard: Flashcard) {
        flashcardDao.create(flashcard)
    }

    fun addFlashcards(arrayListFlashcards: ArrayList<Flashcard>) {
        for (flashcard in arrayListFlashcards) {
            flashcardDao.create(flashcard)
        }
    }

    fun getFlashcardByName(name: String): Flashcard? {
        flashcardList = ArrayList(flashcardDao.queryForEq(Flashcard.columnFlashcardWord, name))
        return if (flashcardList.isNotEmpty()) flashcardList[0] else null
    }

    fun isFirst(): Boolean = getAllFlashcards().size == 1

    fun deleteFlashcard(flashcard: Flashcard) {
        flashcardDao.delete(flashcard)
    }

    fun deleteFlashcards(flashcards: ArrayList<Flashcard>) {
        for (flashcard in flashcards) {
            flashcardDao.delete(flashcard)
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        flashcardDao.update(flashcard)
    }

    fun getFlashcardsByPriority(priority: Int): ArrayList<Flashcard> {
        return ArrayList(flashcardDao.queryForEq(Flashcard.columnFlashcardPriority, priority))
    }

    fun getRandomFlashcardByPririty(priority: Int): Flashcard {
        val flashcards = getFlashcardsByPriority(priority)
        return flashcards.random()
    }

    fun getFlashcardsByCategoryID(categoryID: Int): ArrayList<Flashcard> {
        return ArrayList(flashcardDao.queryForEq(Flashcard.columnFlashcardCategoryID, categoryID))
    }

    fun upFlashcardFailStatistic(flashcard: Flashcard) {
        flashcard.upStaticFail()
        flashcardDao.update(flashcard)
    }

    fun upFlashcardPassStatistic(flashcard: Flashcard) {
        flashcard.upStaticPass()
        flashcardDao.update(flashcard)
    }

    fun upFlashcardPriority(flashcard: Flashcard) {
        flashcard.upPriority()
        flashcardDao.update(flashcard)
    }

    fun downFlashcardPriority(flashcard: Flashcard) {
        flashcard.downPriority()
        flashcardDao.update(flashcard)
    }

    fun resetFlashcardStatistic(flashcard: Flashcard) {
        flashcard.resetStatictic()
        flashcardDao.update(flashcard)
    }

    fun updateFsrsState(flashcard: Flashcard) {
        flashcardDao.update(flashcard)
    }
}
