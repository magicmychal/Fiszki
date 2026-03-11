package click.quickclicker.fiszki

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class ShowCategoryAdapter(
    private val mContext: Context,
    private val rLayout: Int,
    private val arrayList: ArrayList<Category>
) : ArrayAdapter<Category>(mContext, rLayout, arrayList) {

    val choosenCategory = ArrayList<Category>()
    private val flashcardRepository = FlashcardRepository(mContext)

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

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                choosenCategory.add(category)
            } else {
                choosenCategory.remove(category)
            }
        }

        if (choosenCategory.contains(category)) {
            checkBox.isChecked = true
        }
        return view
    }
}
