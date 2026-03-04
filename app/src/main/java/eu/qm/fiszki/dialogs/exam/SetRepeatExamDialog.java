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

public class SetRepeatExamDialog extends MaterialDialog.Builder {

    private Activity mActivity;
    private TextView mCardsText;
    private ArrayList<Integer> mRange;


    public SetRepeatExamDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.title(R.string.exam_repeat_dialog_title);

        init();

        this.items(getCategoriesName());
        this.itemsCallbackSingleChoice(-1, onClickCategory());
    }

    private void init() {
        mCardsText = (TextView) mActivity.findViewById(R.id.exam_repeat_text);
        mRange = new ArrayList<>();
    }

    private ArrayList<Integer> getCategoriesName() {
        mRange.add(5);
        mRange.add(10);
        mRange.add(15);
        mRange.add(25);
        mRange.add(50);
        return mRange;
    }

    private MaterialDialog.ListCallbackSingleChoice onClickCategory() {
        return new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                mCardsText.setText(mRange.get(which).toString());
                return true;
            }
        };
    }


}
