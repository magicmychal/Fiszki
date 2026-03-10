package click.quickclicker.fiszki.dialogs.flashcard

import android.app.Activity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.model.flashcard.ValidationFlashcards

class EditAndDeleteFlashcardDialog(
    private val mActivity: Activity,
    private val mFlashcard: Flashcard
) : MaterialAlertDialogBuilder(mActivity) {

    private val customView = LayoutInflater.from(mActivity).inflate(R.layout.flashcard_edit_dialog, null)
    private val mWordET: TextInputEditText = customView.findViewById(R.id.edit_flashcard_et_word)
    private val mTranslateET: TextInputEditText = customView.findViewById(R.id.edit_flashcard_et_translation)
    private val mFlashcardRepository = FlashcardRepository(mActivity)
    private val mValidationFlashcards = ValidationFlashcards(mActivity)

    init {
        setTitle(R.string.flashcard_edit_title)
        setIcon(ContextCompat.getDrawable(context, R.drawable.ic_pencil_black))
        setView(customView)
        setCancelable(true)
        setNeutralButton(R.string.flashcard_delete_btn, null)
        setPositiveButton(R.string.flashcard_edit_done, null)

        mWordET.setText(mFlashcard.getWord())
        mTranslateET.setText(mFlashcard.getTranslation())
    }

    override fun show(): AlertDialog {
        val dialog = super.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val flashcard = mFlashcard
                flashcard.setWord(mWordET.text.toString().trim())
                flashcard.setTranslation(mTranslateET.text.toString().trim())

                if (mValidationFlashcards.validateAdd(flashcard)) {
                    mFlashcardRepository.updateFlashcard(flashcard)
                    Toast.makeText(context, R.string.flashcard_edit_toast, Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                AlertDialog.Builder(context)
                    .setMessage(R.string.flashcard_delete_message)
                    .setPositiveButton(R.string.button_action_yes) { _, _ ->
                        mindfulSnackbar()
                        mFlashcardRepository.deleteFlashcard(mFlashcard)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.button_action_no) { _, _ -> }
                    .show()
            }
        }
        dialog.show()
        return dialog
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
