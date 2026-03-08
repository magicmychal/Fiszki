package eu.qm.fiszki.dialogs.exam

import android.app.Activity
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository

class SetRangeExamDialog(mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {

    private var mCategories: ArrayList<Category>
    private var mCardsText: TextView
    private var mCategoryRepository: CategoryRepository

    init {
        setTitle(R.string.exam_range_dialog_title)

        mCategoryRepository = CategoryRepository(mActivity)
        mCategories = mCategoryRepository.getAllCategory()
        mCardsText = mActivity.findViewById(R.id.exam_range_text) as TextView

        val names = getCategoriesName().toTypedArray()
        setSingleChoiceItems(names, -1) { _, which ->
            mCardsText.text = mCategories[which].getCategory()
        }
    }

    private fun getCategoriesName(): ArrayList<String> {
        val categoriesName = ArrayList<String>()
        for (cat in mCategories) {
            categoriesName.add(cat.getCategory())
        }
        return categoriesName
    }
}
