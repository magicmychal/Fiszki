package eu.qm.fiszki.dialogs.flashcard

import android.app.Activity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository
import eu.qm.fiszki.model.flashcard.ValidationFlashcards

// todo pokazywanie klawiatury po fokusie

class QuicklyAddFlashcardDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private lateinit var mValidationFlashcards: ValidationFlashcards
    private lateinit var mFlashcardWordET: TextInputEditText
    private lateinit var mFlashcardTranslationET: TextInputEditText
    private lateinit var mFlashcardRepository: FlashcardRepository

    init {
        title(R.string.add_fast_new_flashcard)
        customView(R.layout.flashcard_add_dialog, false)
        positiveText(R.string.add_new_flashcard_btn)
        positiveColor(mActivity.resources.getColor(R.color.ColorPrimaryDark))
        onPositive(addQuicklyFlashcardBtn())
        initViews()
        autoDismiss(false)
        keyboardAction()
    }

    private fun initViews() {
        mFlashcardWordET = customView.findViewById(R.id.add_flashcard_et_word) as TextInputEditText
        mFlashcardTranslationET = customView.findViewById(R.id.add_flashcard_et_translation) as TextInputEditText
        mFlashcardRepository = FlashcardRepository(context)
        mValidationFlashcards = ValidationFlashcards(context)
    }

    private fun keyboardAction() {
        mFlashcardTranslationET.setOnEditorActionListener { _, actionId, keyEvent ->
            if ((keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                addFlashcard()
            }
            false
        }
    }

    private fun addQuicklyFlashcardBtn(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { _, _ ->
            addFlashcard()
        }
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
