package eu.qm.fiszki.dialogs.exam;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import eu.qm.fiszki.R;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;

/**
 * Created by mBoiler on 25.11.2016.
 */

public class SetRangeExamDialog extends MaterialDialog.Builder {

    private Activity mActivity;
    private ArrayList<Category> mCategories;
    private TextView mCardsText;
    private CategoryRepository mCategoryRepository;


    public SetRangeExamDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.title(R.string.exam_range_dialog_title);

        init();

        this.items(getCategoriesName());
        this.itemsCallbackSingleChoice(-1, onClickCategory());
    }

    private void init() {
        mCategoryRepository = new CategoryRepository(mActivity);
        mCategories = mCategoryRepository.getAllCategory();
        mCardsText = (TextView) mActivity.findViewById(R.id.exam_range_text);
    }

    private ArrayList<String> getCategoriesName() {
        ArrayList<String> categoriesName = new ArrayList<>();
        for (Category cat : mCategories) {
            categoriesName.add(cat.getCategory());
        }
        return categoriesName;
    }

    private MaterialDialog.ListCallbackSingleChoice onClickCategory() {
        return new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                mCardsText.setText(mCategories.get(which).getCategory());
                return true;
            }
        };
    }


}
