package eu.qm.fiszki.drawer.drawerItem

import android.app.Activity
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.withDescription
import com.mikepenz.materialdrawer.model.interfaces.withIcon
import com.mikepenz.materialdrawer.model.interfaces.withName
import eu.qm.fiszki.Alert
import eu.qm.fiszki.R
import eu.qm.fiszki.dialogs.category.SelectCategoryDialog
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class SelectCategory(activity: Activity) : PrimaryDrawerItem() {

    init {
        val categoryRepository = CategoryRepository(activity)
        val flashcardRepository = FlashcardRepository(activity)

        val categoryToPopulate = ArrayList<eu.qm.fiszki.model.category.Category>().apply {
            categoryRepository.getCategoryByID(1)?.let { add(it) }
            addAll(categoryRepository.getUserCategory())
        }

        withName(R.string.drawer_select_name)
        withDescription(R.string.drawer_select_sub)
        withIcon(R.drawable.ic_category_select)
        withOnDrawerItemClickListener { _, _, _ ->
            if (flashcardRepository.getAllFlashcards().isEmpty()) {
                Alert().addFiszkiToFeature(activity).show()
            } else {
                SelectCategoryDialog(activity, categoryToPopulate).show()
            }
            false
        }
    }
}
