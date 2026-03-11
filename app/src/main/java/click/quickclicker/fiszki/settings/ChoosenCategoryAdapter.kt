package click.quickclicker.fiszki.settings

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository

class ChoosenCategoryAdapter(
    private val mContext: Context,
    private val rLayout: Int,
    private val arrayList: ArrayList<Category>
) : ArrayAdapter<Category>(mContext, rLayout, arrayList) {

    private val categoryRepository = CategoryRepository(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val category = arrayList[position]
        val inflater = (mContext as Activity).layoutInflater
        val view = inflater.inflate(rLayout, parent, false)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        if (category.id == 1) {
            checkBox.setText(R.string.add_new_flashcard_no_category)
        } else {
            checkBox.text = category.getCategory()
        }

        checkBox.isChecked = category.isChosen

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            category.isChosen = isChecked
            categoryRepository.updateCategory(category)
        }
        return view
    }
}
