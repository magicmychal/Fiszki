package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.DescribableKt;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import java.util.ArrayList;

import eu.qm.fiszki.Alert;
import eu.qm.fiszki.R;
import eu.qm.fiszki.dialogs.category.SelectCategoryDialog;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

/**
 * Created by Siusiacz on 09.07.2016.
 */
public class SelectCategory extends PrimaryDrawerItem {

    CategoryRepository categoryRepository;
    FlashcardRepository flashcardRepository;

    public SelectCategory(final Activity activity) {
        categoryRepository = new CategoryRepository(activity);
        flashcardRepository = new FlashcardRepository(activity);

        final ArrayList<Category> categoryToPopulate = new ArrayList<Category>();
        categoryToPopulate.add(categoryRepository.getCategoryByID(1));
        categoryToPopulate.addAll(categoryRepository.getUserCategory());

        NameableKt.withName(this, R.string.drawer_select_name);
        DescribableKt.withDescription(this, R.string.drawer_select_sub);
        IconableKt.withIcon(this, R.drawable.ic_category_select);
        this.withOnDrawerItemClickListener((view, drawerItem, position) -> {
            if (flashcardRepository.getAllFlashcards().isEmpty()) {
                new Alert().addFiszkiToFeature(activity).show();
            } else {
                new SelectCategoryDialog(activity, categoryToPopulate).show();
            }
            return false;
        });
    }
}
