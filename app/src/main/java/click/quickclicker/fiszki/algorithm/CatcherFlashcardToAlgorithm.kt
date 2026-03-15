package click.quickclicker.fiszki.algorithm

import android.content.Context
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class CatcherFlashcardToAlgorithm(context: Context) {

    private val categoryRepository = CategoryRepository(context)
    private val flashcardRepository = FlashcardRepository(context)

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
