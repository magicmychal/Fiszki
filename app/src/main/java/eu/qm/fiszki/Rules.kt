package eu.qm.fiszki

import android.app.Activity
import android.widget.EditText
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class Rules {

    companion object {
        @JvmStatic
        fun addNewFlashcardRule(
            orginalWord: EditText, translateWord: EditText,
            activity: Activity, categoryId: Int
        ): Boolean {
            val alert = Alert()
            val flashcardRepository = FlashcardRepository(activity.baseContext)

            if (orginalWord.text.toString().isEmpty() || translateWord.text.toString().isEmpty()) {
                alert.buildAlert(
                    activity.getString(R.string.alert_title),
                    activity.getString(R.string.alert_message_onEmptyFields),
                    activity.getString(R.string.button_action_ok), activity
                )
                return false
            }

            if (flashcardRepository.getFlashcardByName(orginalWord.text.toString()) != null) {
                val flashcards = flashcardRepository.getFlashcardsByCategoryID(categoryId)
                for (flashcard in flashcards) {
                    if (flashcard.wordDB == orginalWord.text.toString()) {
                        alert.buildAlert(
                            activity.getString(R.string.alert_title),
                            activity.getString(R.string.alert_message_onRecordExist),
                            activity.getString(R.string.button_action_ok), activity
                        )
                        orginalWord.setText(null as CharSequence?)
                        translateWord.setText(null as CharSequence?)
                        orginalWord.requestFocus()
                        return false
                    }
                }
                return true
            }
            return true
        }
    }
}
