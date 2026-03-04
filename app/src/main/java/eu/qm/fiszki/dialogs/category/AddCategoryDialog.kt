package eu.qm.fiszki.dialogs.category

import android.app.Activity
import android.app.Dialog
import android.widget.ArrayAdapter
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.R
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.category.ValidationCategory

class AddCategoryDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mCategoryNameET: TextInputEditText
    private lateinit var mCategoryLangFrom: MaterialAutoCompleteTextView
    private lateinit var mCategoryLangOn: MaterialAutoCompleteTextView
    private lateinit var mValidationCategory: ValidationCategory

    init {
        title(R.string.category_dialog_title)
        icon(context.resources.getDrawable(R.drawable.ic_category_add))
        customView(R.layout.category_add_dialog, false)
        positiveText(R.string.category_positive_btn_text)
        positiveColor(mActivity.resources.getColor(R.color.ColorPrimaryDark))
        onPositive(addCategoryBtn())
        initViews()
        autoDismiss(false)
        setAdapterToLang()
    }

    private fun initViews() {
        mCategoryNameET = customView.findViewById(R.id.add_category_dialog_et_name) as TextInputEditText
        mCategoryLangFrom = customView.findViewById(R.id.add_category_dialog_lang_from) as MaterialAutoCompleteTextView
        mCategoryLangOn = customView.findViewById(R.id.add_category_dialog_lang_on) as MaterialAutoCompleteTextView
        mCategoryRepository = CategoryRepository(mActivity)
        mValidationCategory = ValidationCategory(mActivity)
    }

    private fun setAdapterToLang() {
        val countries = context.resources.getStringArray(R.array.support_lang)
        val adapter = ArrayAdapter(context, android.R.layout.simple_selectable_list_item, countries)
        mCategoryLangFrom.setAdapter(adapter)
        mCategoryLangOn.setAdapter(adapter)
    }

    private fun addCategoryBtn(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            addCategory(dialog)
        }
    }

    private fun addCategory(dialog: Dialog) {
        val category = Category().apply {
            setCategory(mCategoryNameET.text.toString().trim())
            isEntryByUser = true
            setLangOn(mCategoryLangOn.text.toString().trim())
            setLangFrom(mCategoryLangFrom.text.toString().trim())
        }

        if (mValidationCategory.validate(category)) {
            mCategoryRepository.addCategory(category)
            Toast.makeText(context, R.string.category_toast, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }
}
