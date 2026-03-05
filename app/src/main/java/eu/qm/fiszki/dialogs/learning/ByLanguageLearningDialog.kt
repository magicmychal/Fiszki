package eu.qm.fiszki.dialogs.learning

import android.app.Activity
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ByLanguageLearningDialog(private val mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {

    private val customView = LayoutInflater.from(mActivity).inflate(R.layout.learning_by_lang_dialog, null, false)
    private val mSpinnerFrom: MaterialAutoCompleteTextView = customView.findViewById(R.id.spinner_langFrom)
    private val mSpinnerOn: MaterialAutoCompleteTextView = customView.findViewById(R.id.spinner_langOn)
    private val mCategoryRepository = CategoryRepository(mActivity)
    private val mFlashcardRepository = FlashcardRepository(mActivity)
    private val mLangFromItems: ArrayList<String>
    private val mLangOnItems: ArrayList<String>

    init {
        mLangFromItems = buildLangList { it.getLangFrom() }
        mLangOnItems = buildLangList { it.getLangOn() }

        setTitle(R.string.learning_by_lang_dialog_title)
        setView(customView)
        setCancelable(true)
        setPositiveButton(R.string.learning_by_category_dialog_btn_to_learning, null)
        setNegativeButton(R.string.learning_by_category_dialog_btn_back, null)

        mSpinnerFrom.setAdapter(ArrayAdapter(mActivity, android.R.layout.simple_dropdown_item_1line, mLangFromItems))
        mSpinnerFrom.setText(mLangFromItems.firstOrNull() ?: "", false)

        mSpinnerOn.setAdapter(ArrayAdapter(mActivity, android.R.layout.simple_dropdown_item_1line, mLangOnItems))
        mSpinnerOn.setText(mLangOnItems.firstOrNull() ?: "", false)
    }

    override fun show(): AlertDialog {
        val dialog = super.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val fromText = mSpinnerFrom.text.toString()
                val onText = mSpinnerOn.text.toString()
                val whichever = mActivity.getString(R.string.learning_by_lang_whichever)

                val chosenCategories = ArrayList<Category>()
                when {
                    fromText == whichever && onText == whichever ->
                        chosenCategories.addAll(mCategoryRepository.getAllCategory())
                    fromText == whichever ->
                        chosenCategories.addAll(mCategoryRepository.getCategoryByLangOn(onText))
                    onText == whichever ->
                        chosenCategories.addAll(mCategoryRepository.getCategoryByLangFrom(fromText))
                    else ->
                        chosenCategories.addAll(mCategoryRepository.getCategoryByLang(fromText, onText))
                }

                if (chosenCategories.isEmpty()) {
                    Toast.makeText(mActivity, R.string.learning_by_lang_tost_empty_chose, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val flashcards = ArrayList<Flashcard>()
                for (cat in chosenCategories) {
                    flashcards.addAll(mFlashcardRepository.getFlashcardsByCategoryID(cat.id))
                }
                if (flashcards.isEmpty()) {
                    Toast.makeText(mActivity, R.string.learning_by_lang_tost_empty_chose, Toast.LENGTH_LONG).show()
                } else {
                    dialog.dismiss()
                    ChangeActivityManager(mActivity).goToLearningCheck(flashcards)
                }
            }
        }
        dialog.show()
        return dialog
    }

    private fun buildLangList(selector: (Category) -> String?): ArrayList<String> {
        val set = LinkedHashSet<String>()
        for (cat in mCategoryRepository.getUserCategory()) {
            val value = selector(cat)
            if (!value.isNullOrEmpty()) set.add(value)
        }
        val result = ArrayList<String>()
        result.add(mActivity.getString(R.string.learning_by_lang_whichever))
        result.addAll(set)
        return result
    }
}
