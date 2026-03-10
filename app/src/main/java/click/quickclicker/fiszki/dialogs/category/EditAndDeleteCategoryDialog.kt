package click.quickclicker.fiszki.dialogs.category

import android.app.Activity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.category.ValidationCategory
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class EditAndDeleteCategoryDialog(
    private val mActivity: Activity,
    private val mCategory: Category
) : MaterialAlertDialogBuilder(mActivity) {

    private val customView = LayoutInflater.from(mActivity).inflate(R.layout.category_add_dialog, null, false)
    private val mCategoryNameET: TextInputEditText = customView.findViewById(R.id.add_category_dialog_et_name)
    private val mCategoryLangFrom: MaterialAutoCompleteTextView = customView.findViewById(R.id.add_category_dialog_lang_from)
    private val mCategoryLangOn: MaterialAutoCompleteTextView = customView.findViewById(R.id.add_category_dialog_lang_on)
    private val mValidationCategory = ValidationCategory(mActivity)
    private val mCategoryRepository = CategoryRepository(mActivity)
    private val mFlashcardRepository = FlashcardRepository(mActivity)
    private var mFlashcards: ArrayList<Flashcard>? = null

    init {
        setTitle(R.string.edit_category_title)
        setIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_pencil_black))
        setView(customView)
        setCancelable(true)
        setPositiveButton(R.string.edit_category_done, null)
        setNeutralButton(R.string.edit_category_delete, null)

        mCategoryNameET.setText(mCategory.getCategory())
        mCategoryLangFrom.setText(mCategory.getLangFrom() ?: "")
        mCategoryLangOn.setText(mCategory.getLangOn() ?: "")
    }

    override fun show(): AlertDialog {
        val dialog = super.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val category = mCategory
                category.setCategory(mCategoryNameET.text.toString().trim())
                category.setLangFrom(mCategoryLangFrom.text.toString().trim())
                category.setLangOn(mCategoryLangOn.text.toString().trim())

                if (mValidationCategory.validate(category)) {
                    mCategoryRepository.updateCategory(category)
                    Toast.makeText(mActivity, R.string.edit_category_toast, Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                MaterialAlertDialogBuilder(mActivity)
                    .setMessage(R.string.edit_category_delete_message)
                    .setPositiveButton(R.string.button_action_yes) { _, _ ->
                        deleteCategoryWithFlashcards()
                        mindfulSnackbar()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.button_action_no, null)
                    .show()
            }
        }
        dialog.show()
        return dialog
    }

    private fun deleteCategoryWithFlashcards() {
        mFlashcards = mFlashcardRepository.getFlashcardsByCategoryID(mCategory.id)
        mFlashcards?.takeIf { it.isNotEmpty() }?.let { mFlashcardRepository.deleteFlashcards(it) }
        mCategoryRepository.deleteCategory(mCategory)
    }

    private fun mindfulSnackbar() {
        Snackbar.make(mActivity.currentFocus!!, R.string.snackbar_return_category_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_return_word_button) {
                mCategoryRepository.addCategory(mCategory)
                mFlashcards?.let { mFlashcardRepository.addFlashcards(it) }
                mActivity.onWindowFocusChanged(true)
            }
            .show()
    }
}
