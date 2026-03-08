package eu.qm.fiszki.dialogs.learning

import android.app.Activity
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ByCategoryLearningDialog(private val mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {

    private val mCategoryRepository = CategoryRepository(mActivity)
    private val mFlashcardRepository = FlashcardRepository(mActivity)
    private val mAllCategories: ArrayList<Category>
    private val mCheckedItems: BooleanArray

    init {
        mAllCategories = mCategoryRepository.getAllCategory()
        mCheckedItems = BooleanArray(mAllCategories.size) { false }
        val categoryNames = mAllCategories.map { it.getCategory() }.toTypedArray()

        setTitle(R.string.learning_by_category_dialog_title)
        setCancelable(true)
        setMultiChoiceItems(categoryNames, mCheckedItems) { _, which, isChecked ->
            mCheckedItems[which] = isChecked
        }
        setPositiveButton(R.string.learning_by_category_dialog_btn_to_learning) { _, _ ->
            val chosenCategories = mAllCategories.filterIndexed { i, _ -> mCheckedItems[i] }
            if (chosenCategories.isEmpty()) {
                Toast.makeText(mActivity, R.string.learning_by_category_tost_no_chose, Toast.LENGTH_LONG).show()
                return@setPositiveButton
            }
            val flashcards = ArrayList<Flashcard>()
            for (cat in chosenCategories) {
                flashcards.addAll(mFlashcardRepository.getFlashcardsByCategoryID(cat.id))
            }
            if (flashcards.isEmpty()) {
                Toast.makeText(mActivity, R.string.learning_by_category_tost_empty_category, Toast.LENGTH_LONG).show()
            } else {
                ChangeActivityManager(mActivity).goToLearningCheck(flashcards)
            }
        }
        setNegativeButton(R.string.learning_by_category_dialog_btn_back, null)
    }
}
