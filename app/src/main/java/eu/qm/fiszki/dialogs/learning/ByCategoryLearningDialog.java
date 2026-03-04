package eu.qm.fiszki.dialogs.learning;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import eu.qm.fiszki.FirebaseManager;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.ChangeActivityManager;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

/**
 * Created by mBoiler on 21.11.2016.
 */

public class ByCategoryLearningDialog extends MaterialDialog.Builder {

    private Activity mActivity;
    private CategoryRepository mCategoryRepository;
    private FlashcardRepository mFlashcardRepository;
    private ArrayList<Category> mChoseCategories;
    private ArrayList<Category> mNoEmptyCategories;
    private ArrayList<Flashcard> mFlashcards;

    public ByCategoryLearningDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.title(R.string.learning_by_category_dialog_title);
        this.autoDismiss(false);
        this.neutralText(R.string.learning_by_category_dialog_btn_back);
        this.onNeutral(closeDialog());
        this.positiveText(R.string.learning_by_category_dialog_btn_to_learning);
        this.positiveColor(mActivity.getResources().getColor(R.color.ColorPrimaryDark));
        this.itemsCallbackMultiChoice(null, onChose());
        init();
        fillListView();
    }

    private void init() {
        mFlashcards = new ArrayList<>();
        mChoseCategories = new ArrayList<>();
        mCategoryRepository = new CategoryRepository(mActivity);
        mFlashcardRepository = new FlashcardRepository(mActivity);
    }

    private void fillListView() {
        ArrayList<String> categoryName = new ArrayList<>();
        for (Category cat : mCategoryRepository.getAllCategory()) {
            categoryName.add(cat.getCategory());
        }
        this.items(categoryName);
    }

    private MaterialDialog.ListCallbackMultiChoice onChose() {
        return new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                if (which.length == 0) {
                    Toast.makeText(context, R.string.learning_by_category_tost_no_chose,
                            Toast.LENGTH_LONG).show();
                } else {
                    getCategoryFromList(which);
                    setFlashcards();
                    if (mFlashcards.isEmpty()) {
                        Toast.makeText(context, R.string.learning_by_category_tost_empty_category,
                                Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
                        new FirebaseManager(mActivity).sendEvent(FirebaseManager.Params.LEARNING_CAT);
                        new ChangeActivityManager(mActivity).goToLearningCheck(mFlashcards);
                    }
                }
                return false;
            }
        };
    }

    private void getCategoryFromList(Integer[] which) {
        ArrayList<Category> allCategory = mCategoryRepository.getAllCategory();
        for (int position : which) {
            mChoseCategories.add(allCategory.get(position));
        }
    }

    private void setFlashcards() {
        for (Category cat : mChoseCategories) {
            mFlashcards.addAll(mFlashcardRepository.getFlashcardsByCategoryID(cat.getId()));
        }
    }

    private MaterialDialog.SingleButtonCallback closeDialog() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        };
    }
}
