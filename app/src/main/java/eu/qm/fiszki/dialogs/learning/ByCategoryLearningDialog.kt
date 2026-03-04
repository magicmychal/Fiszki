package eu.qm.fiszki.dialogs.learning

import android.app.Activity
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ByCategoryLearningDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardRepository: FlashcardRepository
    private var mChoseCategories: ArrayList<Category> = ArrayList()
    private var mFlashcards: ArrayList<Flashcard> = ArrayList()

    init {
        title(R.string.learning_by_category_dialog_title)
        autoDismiss(false)
        neutralText(R.string.learning_by_category_dialog_btn_back)
        onNeutral(closeDialog())
        positiveText(R.string.learning_by_category_dialog_btn_to_learning)
        positiveColor(mActivity.resources.getColor(R.color.ColorPrimaryDark))
        itemsCallbackMultiChoice(null, onChose())
        initRepos()
        fillListView()
    }

    private fun initRepos() {
        mCategoryRepository = CategoryRepository(mActivity)
        mFlashcardRepository = FlashcardRepository(mActivity)
    }

    private fun fillListView() {
        val categoryName = ArrayList<String>()
        for (cat in mCategoryRepository.getAllCategory()) {
            categoryName.add(cat.getCategory())
        }
        items(categoryName)
    }

    private fun onChose(): MaterialDialog.ListCallbackMultiChoice {
        return MaterialDialog.ListCallbackMultiChoice { dialog, which, _ ->
            if (which.isEmpty()) {
                Toast.makeText(context, R.string.learning_by_category_tost_no_chose, Toast.LENGTH_LONG).show()
            } else {
                getCategoryFromList(which)
                setFlashcards()
                if (mFlashcards.isEmpty()) {
                    Toast.makeText(context, R.string.learning_by_category_tost_empty_category, Toast.LENGTH_LONG).show()
                } else {
                    dialog.dismiss()
                    ChangeActivityManager(mActivity).goToLearningCheck(mFlashcards)
                }
            }
            false
        }
    }

    private fun getCategoryFromList(which: Array<Int>) {
        val allCategory = mCategoryRepository.getAllCategory()
        for (position in which) {
            mChoseCategories.add(allCategory[position])
        }
    }

    private fun setFlashcards() {
        for (cat in mChoseCategories) {
            mFlashcards.addAll(mFlashcardRepository.getFlashcardsByCategoryID(cat.id))
        }
    }

    private fun closeDialog(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            dialog.dismiss()
        }
    }
}
