package click.quickclicker.fiszki.dialogs.category

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.CATEGORY_COLORS
import click.quickclicker.fiszki.activity.CategoryColor
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.category.ValidationCategory

class AddCategoryDialog(private val mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {

    private val customView: View = LayoutInflater.from(mActivity).inflate(R.layout.category_add_dialog, null, false)
    private val mCategoryNameET: TextInputEditText = customView.findViewById(R.id.add_category_dialog_et_name)
    private val mCategoryLangFrom: MaterialAutoCompleteTextView = customView.findViewById(R.id.add_category_dialog_lang_from)
    private val mCategoryLangOn: MaterialAutoCompleteTextView = customView.findViewById(R.id.add_category_dialog_lang_on)
    private val mCategoryRepository = CategoryRepository(mActivity)
    private val mValidationCategory = ValidationCategory(mActivity)
    private var mSelectedColor: CategoryColor = defaultCategoryColor()
    private val mColorViews = mutableListOf<View>()

    init {
        setTitle(R.string.category_dialog_title)
        setIcon(ContextCompat.getDrawable(context, R.drawable.ic_category_add))
        setView(customView)
        setCancelable(true)
        setPositiveButton(R.string.category_positive_btn_text, null)

        setAdapterToLang()
        buildColorPicker()
    }

    override fun show(): AlertDialog {
        val dialog = super.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                addCategory(dialog)
            }
        }
        dialog.show()
        return dialog
    }

    private fun setAdapterToLang() {
        val countries = context.resources.getStringArray(R.array.support_lang)
        val adapter = ArrayAdapter(context, android.R.layout.simple_selectable_list_item, countries)
        mCategoryLangFrom.setAdapter(adapter)
        mCategoryLangOn.setAdapter(adapter)
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

    private fun addCategory(dialog: AlertDialog) {
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
