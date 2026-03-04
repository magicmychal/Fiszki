package eu.qm.fiszki.dialogs.exam

import android.app.Activity
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository

class SetRangeExamDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private var mCategories: ArrayList<Category>
    private var mCardsText: TextView
    private var mCategoryRepository: CategoryRepository

    init {
        title(R.string.exam_range_dialog_title)

        mCategoryRepository = CategoryRepository(mActivity)
        mCategories = mCategoryRepository.getAllCategory()
        mCardsText = mActivity.findViewById(R.id.exam_range_text) as TextView

        items(getCategoriesName())
        itemsCallbackSingleChoice(-1, onClickCategory())
    }

    private fun getCategoriesName(): ArrayList<String> {
        val categoriesName = ArrayList<String>()
        for (cat in mCategories) {
            categoriesName.add(cat.getCategory())
        }
        return categoriesName
    }

    private fun onClickCategory(): MaterialDialog.ListCallbackSingleChoice {
        return MaterialDialog.ListCallbackSingleChoice { _, _, which, _ ->
            mCardsText.text = mCategories[which].getCategory()
            true
        }
    }
}
