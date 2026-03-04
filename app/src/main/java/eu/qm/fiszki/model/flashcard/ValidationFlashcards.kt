package eu.qm.fiszki.model.flashcard

import android.content.Context
import android.widget.Toast
import eu.qm.fiszki.R

class ValidationFlashcards(private val context: Context) {

    fun validateAdd(flashcard: Flashcard): Boolean {
        if (flashcard.wordDB.isEmpty() || flashcard.translationDB.isEmpty()) {
            Toast.makeText(context, R.string.validation_flashcard_empty, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
