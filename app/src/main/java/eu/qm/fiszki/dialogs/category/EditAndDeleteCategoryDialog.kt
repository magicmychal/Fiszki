package eu.qm.fiszki.dialogs.category

import android.app.Activity
import android.app.AlertDialog
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.R
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.category.ValidationCategory
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class EditAndDeleteCategoryDialog(
    private val mActivity: Activity,
    private val mCategory: Category
) : MaterialDialog.Builder(mActivity) {

    private lateinit var mCategoryNameET: TextInputEditText
    private lateinit var mValidationCategory: ValidationCategory
    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mCategoryLangOn: MaterialAutoCompleteTextView
    private lateinit var mCategoryLangFrom: MaterialAutoCompleteTextView
    private var mFlashcards: ArrayList<Flashcard>? = null

    init {
        title(R.string.edit_category_title)
        icon(mActivity.resources.getDrawable(R.drawable.ic_pencil_black))
        customView(R.layout.category_add_dialog, false)
        autoDismiss(false)
        positiveText(R.string.edit_category_done)
        positiveColor(mActivity.resources.getColor(R.color.ColorPrimaryDark))
        neutralText(R.string.edit_category_delete)
        neutralColor(mActivity.resources.getColor(R.color.md_red_A700))

        onPositive(editCategory())
        onNeutral(deleteCategory())

        initViews()

        mCategoryNameET.setText(mCategory.getCategory())
        if (mCategoryLangFrom != null || mCategoryLangOn != null) {
            mCategoryLangFrom.setText(mCategory.getLangFrom())
            mCategoryLangOn.setText(mCategory.getLangOn())
        } else {
            mCategoryLangFrom.setText("")
            mCategoryLangOn.setText("")
        }
    }

    private fun initViews() {
        mCategoryNameET = customView.findViewById(R.id.add_category_dialog_et_name) as TextInputEditText
        mCategoryLangFrom = customView.findViewById(R.id.add_category_dialog_lang_from) as MaterialAutoCompleteTextView
        mCategoryLangOn = customView.findViewById(R.id.add_category_dialog_lang_on) as MaterialAutoCompleteTextView
        mValidationCategory = ValidationCategory(context)
        mCategoryRepository = CategoryRepository(context)
        mFlashcardRepository = FlashcardRepository(mActivity)
    }

    private fun editCategory(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            val category = mCategory
            category.setCategory(mCategoryNameET.text.toString().trim())
            category.setLangFrom(mCategoryLangFrom.text.toString().trim())
            category.setLangOn(mCategoryLangOn.text.toString().trim())

            if (mValidationCategory.validate(category)) {
                mCategoryRepository.updateCategory(category)
                Toast.makeText(context, R.string.edit_category_toast, Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }
    }

    private fun deleteCategory(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            val editDialog = dialog

            AlertDialog.Builder(context)
                .setMessage(R.string.edit_category_delete_message)
                .setPositiveButton(R.string.button_action_yes) { _, _ ->
                    deleteCategoryWithFlashcards()
                    mindfulSnackbar()
                    editDialog.dismiss()
                }
                .setNegativeButton(R.string.button_action_no) { _, _ -> }
                .show()
        }
    }

    private fun deleteCategoryWithFlashcards() {
        mFlashcards = mFlashcardRepository.getFlashcardsByCategoryID(mCategory.id)
        val flashcards = mFlashcards
        if (flashcards != null && flashcards.isNotEmpty()) {
            mFlashcardRepository.deleteFlashcards(flashcards)
        }
        mCategoryRepository.deleteCategory(mCategory)
    }

    private fun mindfulSnackbar() {
        Snackbar.make(mActivity.currentFocus!!, R.string.snackbar_return_category_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_return_word_button) {
                mCategoryRepository.addCategory(mCategory)
                val savedFlashcards = mFlashcards
                if (savedFlashcards != null) {
                    mFlashcardRepository.addFlashcards(savedFlashcards)
                }
                mActivity.onWindowFocusChanged(true)
            }
            .show()
    }
}
