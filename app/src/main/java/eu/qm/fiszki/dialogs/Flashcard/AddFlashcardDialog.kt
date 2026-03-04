package eu.qm.fiszki.dialogs.flashcard

import android.app.Activity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository
import eu.qm.fiszki.model.flashcard.ValidationFlashcards

class AddFlashcardDialog(
    private val mActivity: Activity,
    private val mCategoryId: Int
) {
    private lateinit var mWordEt: TextInputEditText
    private lateinit var mTranslateEt: TextInputEditText
    private val mFlashcardRepository = FlashcardRepository(mActivity)
    private val mValidationFlashcards = ValidationFlashcards(mActivity)

    fun show() {
        val view = LayoutInflater.from(mActivity).inflate(R.layout.flashcard_add_dialog, null)
        mWordEt = view.findViewById(R.id.add_flashcard_et_word)
        mTranslateEt = view.findViewById(R.id.add_flashcard_et_translation)

        val dialog = MaterialAlertDialogBuilder(mActivity)
            .setTitle(R.string.flashcard_add_title)
            .setView(view)
            .setPositiveButton(R.string.action_confirm, null)
            .setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                addFlashcard()
            }
        }

        mTranslateEt.setOnEditorActionListener { _, actionId, keyEvent ->
            if ((keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                addFlashcard()
            }
            false
        }

        dialog.show()
    }

    private fun addFlashcard() {
        val flashcard = Flashcard().apply {
            setWord(mWordEt.text.toString().trim())
            setTranslation(mTranslateEt.text.toString().trim())
            categoryID = mCategoryId
            priority = 1
        }

        if (mValidationFlashcards.validateAdd(flashcard)) {
            mFlashcardRepository.addFlashcard(flashcard)
            Toast.makeText(mActivity, R.string.add_new_flashcard_toast, Toast.LENGTH_LONG).show()
            mTranslateEt.setText(null)
            mWordEt.setText(null)
        }
        mWordEt.requestFocus()
    }
}
