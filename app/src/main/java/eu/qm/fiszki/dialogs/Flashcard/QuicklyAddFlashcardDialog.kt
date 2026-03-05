package eu.qm.fiszki.dialogs.flashcard

import android.app.Activity
import android.view.LayoutInflater
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository
import eu.qm.fiszki.model.flashcard.ValidationFlashcards

class QuicklyAddFlashcardDialog(mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {

    private val customView = LayoutInflater.from(context).inflate(R.layout.flashcard_add_dialog, null, false)
    private val mFlashcardWordET: TextInputEditText = customView.findViewById(R.id.add_flashcard_et_word)
    private val mFlashcardTranslationET: TextInputEditText = customView.findViewById(R.id.add_flashcard_et_translation)
    private val mFlashcardRepository = FlashcardRepository(mActivity)
    private val mValidationFlashcards = ValidationFlashcards(mActivity)

    init {
        setTitle(R.string.add_fast_new_flashcard)
        setView(customView)
        setCancelable(true)
        setPositiveButton(R.string.add_new_flashcard_btn, null)

        mFlashcardTranslationET.setOnEditorActionListener { _, actionId, keyEvent ->
            if ((keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                addFlashcard()
            }
            false
        }
    }

    override fun show(): androidx.appcompat.app.AlertDialog {
        val dialog = super.create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                addFlashcard()
            }
        }
        dialog.show()
        return dialog
    }

    private fun addFlashcard() {
        val flashcard = Flashcard().apply {
            setWord(mFlashcardWordET.text.toString().trim())
            setTranslation(mFlashcardTranslationET.text.toString().trim())
            priority = 1
            categoryID = 1
        }

        if (mValidationFlashcards.validateAdd(flashcard)) {
            mFlashcardRepository.addFlashcard(flashcard)
            Toast.makeText(context, R.string.add_new_flashcard_toast, Toast.LENGTH_SHORT).show()
            mFlashcardWordET.setText("")
            mFlashcardTranslationET.setText("")
            mFlashcardWordET.requestFocus()
        }
    }
}
