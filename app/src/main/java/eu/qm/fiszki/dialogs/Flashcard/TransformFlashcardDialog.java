package eu.qm.fiszki.dialogs.flashcard;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

import eu.qm.fiszki.R;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;
import eu.qm.fiszki.activity.myWords.CategoryManagerSingleton;
import eu.qm.fiszki.activity.myWords.flashcards.SelectedFlashcardsSingleton;


/**
 * Created by mBoiler on 19.11.2016.
 */

public class TransformFlashcardDialog extends MaterialDialog.Builder {

    private Activity mActivity;
    private MaterialSpinner mSpinner;
    private Category seletedCategory;
    private CategoryRepository mCategoryRepository;

    public TransformFlashcardDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.title(R.string.flashcard_transform_title);
        this.icon(activity.getResources().getDrawable(R.drawable.ic_transform_black));
        this.customView(R.layout.flashcard_transform_dialog, false);
        this.positiveText(R.string.flashcard_transform_button);
        this.positiveColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
        this.onPositive(changeCategory());
        init();
        buildSpinner();
    }

    private void init() {
        mCategoryRepository = new CategoryRepository(mActivity);
    }

    private void buildSpinner() {
        int cuntPosition = 0;
        boolean findPosition = false;
        final ArrayList<Category> categories = mCategoryRepository.getUserCategory();
        ArrayList<String> spinnerCategories = new ArrayList<>();
        for (Category cat : categories) {
            if (!findPosition) {
                if (cat.getId() == CategoryManagerSingleton.getCurrentCategoryId()) {
                    findPosition = true;
                } else {
                    cuntPosition++;
                }
            }
            spinnerCategories.add(cat.getCategory());
        }

        //zabezpieczenie przed FC; Kiedy przenosimy z braku kategori
        if (!findPosition) {
            cuntPosition = 0;
        }

        mSpinner = (MaterialSpinner) customView.findViewById(R.id.transform_spinner);
        mSpinner.setItems(spinnerCategories);
        mSpinner.setSelectedIndex(cuntPosition);
        mSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                seletedCategory = categories.get(position);
            }
        });
    }

    private MaterialDialog.SingleButtonCallback changeCategory() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                FlashcardRepository flashcardRepository = new FlashcardRepository(mActivity);
                ArrayList<Flashcard> flashcards = SelectedFlashcardsSingleton.getFlashcards();

                //zabezpieczenie przec FC; Gdy jest jedna kategoria i nie trzeba wybierać;
                if (seletedCategory == null) {
                    seletedCategory = mCategoryRepository.getUserCategory().get(0);
                }

                for (Flashcard card : flashcards) {
                    card.setCategoryID(seletedCategory.getId());
                    flashcardRepository.updateFlashcard(card);
                }
            }
        };
    }
}
