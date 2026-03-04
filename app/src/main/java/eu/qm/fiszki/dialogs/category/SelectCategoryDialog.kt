package eu.qm.fiszki.dialogs.category

import android.content.Context
import android.widget.ListView
import android.widget.RelativeLayout
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.settings.ChoosenCategoryAdapter

class SelectCategoryDialog(
    context: Context,
    categoryToPopulate: ArrayList<Category>
) : MaterialDialog.Builder(context) {

    init {
        title(R.string.drawer_select_name)
        customView(R.layout.category_choose, false)
        positiveText(R.string.button_action_ok)

        // populate listview
        val listView = customView.findViewById(R.id.choose_category_lv) as ListView
        val choosenCategoryAdapter = ChoosenCategoryAdapter(
            context,
            R.layout.category_choose_adapter,
            categoryToPopulate
        )
        listView.adapter = choosenCategoryAdapter

        // limit of height
        if (listView.adapter.count >= 6) {
            val lp = listView.layoutParams as RelativeLayout.LayoutParams
            lp.height = 1000
            listView.layoutParams = lp
        }
    }
}
