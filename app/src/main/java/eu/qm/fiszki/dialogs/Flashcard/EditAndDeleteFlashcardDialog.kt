package eu.qm.fiszki.dialogs.flashcard

import android.app.Activity
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository
import eu.qm.fiszki.model.flashcard.ValidationFlashcards

class EditAndDeleteFlashcardDialog(
    private val mActivity: Activity,
    private val mFlashcard: Flashcard
) : MaterialDialog.Builder(mActivity) {

    private lateinit var mWordET: TextInputEditText
    private lateinit var mTranslateET: TextInputEditText
    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mValidationFlashcards: ValidationFlashcards

    init {
        title(R.string.flashcard_edit_title)
        customView(R.layout.flashcard_edit_dialog, false)
        icon(ContextCompat.getDrawable(context, R.drawable.ic_pencil_black)!!)
        autoDismiss(false)
        neutralText(R.string.flashcard_delete_btn)
        neutralColor(ContextCompat.getColor(context, R.color.md_red_A700))
        positiveText(R.string.flashcard_edit_done)
        val typedValue = TypedValue()
        mActivity.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        positiveColor(typedValue.data)

        onPositive(editFlashcard())
        onNeutral(deleteFlashcard())

        initViews()

        mWordET.setText(mFlashcard.getWord())
        mTranslateET.setText(mFlashcard.getTranslation())
    }

    private fun initViews() {
        mTranslateET = customView.findViewById(R.id.edit_flashcard_et_translation) as TextInputEditText
        mWordET = customView.findViewById(R.id.edit_flashcard_et_word) as TextInputEditText
        mValidationFlashcards = ValidationFlashcards(context)
        mFlashcardRepository = FlashcardRepository(context)
    }

    private fun editFlashcard(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            val flashcard = mFlashcard
            flashcard.setWord(mWordET.text.toString().trim())
            flashcard.setTranslation(mTranslateET.text.toString().trim())

            if (mValidationFlashcards.validateAdd(flashcard)) {
                mFlashcardRepository.updateFlashcard(flashcard)
                Toast.makeText(context, R.string.flashcard_edit_toast, Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }
    }

    private fun deleteFlashcard(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            val editDialog = dialog

            AlertDialog.Builder(context)
                .setMessage(R.string.flashcard_delete_message)
                .setPositiveButton(R.string.button_action_yes) { _, _ ->
                    mindfulSnackbar()
                    mFlashcardRepository.deleteFlashcard(mFlashcard)
                    editDialog.dismiss()
                }
                .setNegativeButton(R.string.button_action_no) { _, _ -> }
                .show()
        }
    }

    private fun mindfulSnackbar() {
        Snackbar.make(mActivity.currentFocus!!, R.string.snackbar_return_category_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_return_word_button) {
                mFlashcardRepository.addFlashcard(mFlashcard)
                mActivity.onWindowFocusChanged(true)
            }
            .show()
    }
}
