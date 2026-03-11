package click.quickclicker.fiszki.dialogs.category

import android.content.Context
import android.view.LayoutInflater
import android.widget.ListView
import android.widget.RelativeLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.settings.ChoosenCategoryAdapter

class SelectCategoryDialog(
    context: Context,
    categoryToPopulate: ArrayList<Category>
) : MaterialAlertDialogBuilder(context) {

    init {
        setTitle(R.string.drawer_select_name)
        val customView = LayoutInflater.from(context).inflate(R.layout.category_choose, null, false)
        setView(customView)
        setPositiveButton(R.string.button_action_ok, null)

        val listView = customView.findViewById<ListView>(R.id.choose_category_lv)
        val choosenCategoryAdapter = ChoosenCategoryAdapter(
            context,
            R.layout.category_choose_adapter,
            categoryToPopulate
        )
        listView.adapter = choosenCategoryAdapter

        if (listView.adapter.count >= 6) {
            val lp = listView.layoutParams as RelativeLayout.LayoutParams
            lp.height = 1000
            listView.layoutParams = lp
        }
    }
}
