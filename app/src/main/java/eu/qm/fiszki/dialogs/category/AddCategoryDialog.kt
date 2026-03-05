package eu.qm.fiszki.dialogs.category

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.CATEGORY_COLORS
import eu.qm.fiszki.activity.CategoryColor
import eu.qm.fiszki.activity.defaultCategoryColor
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.category.ValidationCategory

class AddCategoryDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mCategoryNameET: TextInputEditText
    private lateinit var mCategoryLangFrom: MaterialAutoCompleteTextView
    private lateinit var mCategoryLangOn: MaterialAutoCompleteTextView
    private lateinit var mValidationCategory: ValidationCategory
    private var mSelectedColor: CategoryColor = defaultCategoryColor()
    private val mColorViews = mutableListOf<View>()

    init {
        title(R.string.category_dialog_title)
        icon(ContextCompat.getDrawable(context, R.drawable.ic_category_add)!!)
        customView(R.layout.category_add_dialog, false)
        positiveText(R.string.category_positive_btn_text)
        val typedValue = TypedValue()
        mActivity.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        positiveColor(typedValue.data)
        onPositive(addCategoryBtn())
        initViews()
        autoDismiss(false)
        setAdapterToLang()
        buildColorPicker()
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

    private fun buildColorPicker() {
        val container = customView.findViewById<LinearLayout>(R.id.add_category_color_picker)
        mColorViews.clear()
        val density = context.resources.displayMetrics.density
        val sizePx = (36 * density).toInt()
        val marginPx = (8 * density).toInt()
        val strokePx = (3 * density).toInt()

        for (catColor in CATEGORY_COLORS) {
            val circleView = View(context)
            val params = LinearLayout.LayoutParams(sizePx, sizePx)
            params.marginEnd = marginPx
            circleView.layoutParams = params

            updateCircleDrawable(circleView, catColor, catColor == mSelectedColor, strokePx)

            circleView.setOnClickListener {
                mSelectedColor = catColor
                for ((i, v) in mColorViews.withIndex()) {
                    updateCircleDrawable(v, CATEGORY_COLORS[i], CATEGORY_COLORS[i] == mSelectedColor, strokePx)
                }
            }

            mColorViews.add(circleView)
            container.addView(circleView)
        }
    }

    private fun updateCircleDrawable(view: View, catColor: CategoryColor, isSelected: Boolean, strokePx: Int) {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(catColor.primary)
        if (isSelected) {
            val tv = TypedValue()
            mActivity.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, tv, true)
            drawable.setStroke(strokePx, tv.data)
        }
        view.background = drawable
    }

    private fun addCategory(dialog: Dialog) {
        val category = Category().apply {
            setCategory(mCategoryNameET.text.toString().trim())
            isEntryByUser = true
            setLangOn(mCategoryLangOn.text.toString().trim())
            setLangFrom(mCategoryLangFrom.text.toString().trim())
            setColor(String.format("#%06X", 0xFFFFFF and mSelectedColor.primary))
        }

        if (mValidationCategory.validate(category)) {
            mCategoryRepository.addCategory(category)
            Toast.makeText(context, R.string.category_toast, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }
}
