package eu.qm.fiszki.algorithm

import android.content.Context
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class CatcherFlashcardToAlgorithm(context: Context) {

    private val categoryRepository = CategoryRepository(context)
    private val flashcardRepository = FlashcardRepository(context)
    private val draw = Drawer()

    fun getFlashcardToAlgoritmByPriority(priority: Int, flashcards: ArrayList<Flashcard>): Flashcard {
        return flashcards[draw.drawInteger(flashcards.size)]
    }

    fun getFlashcardsFromChosenCategoryToNotification(): ArrayList<Flashcard> {
        val flashcards = ArrayList<Flashcard>()
        val categories = categoryRepository.getChosenCategory()
        for (category in categories) {
            flashcards.addAll(flashcardRepository.getFlashcardsByCategoryID(category.id))
        }
        return flashcards
    }

    fun getFlashcardsFromChosenCategory(chosenCategory: ArrayList<Category>): ArrayList<Flashcard> {
        val flashcards = ArrayList<Flashcard>()
        for (category in chosenCategory) {
            flashcards.addAll(flashcardRepository.getFlashcardsByCategoryID(category.id))
        }
        return flashcards
    }
}
