package eu.qm.fiszki.dialogs.learning;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.ChangeActivityManager;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

/**
 * Created by mBoiler on 21.11.2016.
 */

public class ByLanguageLearningDialog extends MaterialDialog.Builder {

    private Activity mActivity;
    private MaterialSpinner mSpinnerFrom;
    private MaterialSpinner mSpinnerOn;
    private CategoryRepository mCategoryRepository;
    private FlashcardRepository mFlashcardRepository;
    private ArrayList<Category> mLearningCategory;
    private ArrayList<Flashcard> mLearningFlashcards;

    public ByLanguageLearningDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.title(R.string.learning_by_lang_dialog_title);
        this.customView(R.layout.learning_by_lang_dialog, false);
        this.autoDismiss(false);
        this.neutralText(R.string.learning_by_category_dialog_btn_back);
        this.onNeutral(closeDialog());
        this.positiveText(R.string.learning_by_category_dialog_btn_to_learning);
        this.positiveColor(mActivity.getResources().getColor(R.color.ColorPrimaryDark));
        this.onPositive(goLearning());
        init();
        buildSpinnerFrom();
        buildSpinnerOn();
    }

    private void init() {
        mLearningCategory = new ArrayList<Category>();
        mLearningFlashcards = new ArrayList<Flashcard>();
        mCategoryRepository = new CategoryRepository(mActivity);
        mFlashcardRepository = new FlashcardRepository(mActivity);
    }

    private void buildSpinnerFrom() {
        mSpinnerFrom = (MaterialSpinner) customView.findViewById(R.id.spinner_langFrom);
        ArrayList<String> langFromArray = new ArrayList<>();
        for (Category cat : mCategoryRepository.getUserCategory()) {
            if (cat.getLangFrom() != null && !cat.getLangFrom().isEmpty()) {
                langFromArray.add(cat.getLangFrom());
            }
        }
        //delete recurrence
        Set<String> hs = new HashSet<>();
        hs.addAll(langFromArray);
        langFromArray.clear();
        langFromArray.add(context.getString(R.string.learning_by_lang_whichever));
        langFromArray.addAll(hs);
        mSpinnerFrom.setItems(langFromArray);
    }

    private void buildSpinnerOn() {
        mSpinnerOn = (MaterialSpinner) customView.findViewById(R.id.spinner_langOn);
        ArrayList<String> langOnArray = new ArrayList<>();
        for (Category cat : mCategoryRepository.getUserCategory()) {
            if (cat.getLangOn() != null && !cat.getLangOn().isEmpty()) {
                langOnArray.add(cat.getLangOn());
            }
        }
        //delete recurrence
        Set<String> hs = new HashSet<>();
        hs.addAll(langOnArray);
        langOnArray.clear();
        langOnArray.add(context.getString(R.string.learning_by_lang_whichever));
        langOnArray.addAll(hs);
        mSpinnerOn.setItems(langOnArray);
    }

    private MaterialDialog.SingleButtonCallback goLearning() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                setChosenLang();
                if (mLearningCategory.isEmpty() || mLearningCategory == null) {
                    Toast.makeText(context, R.string.learning_by_lang_tost_empty_chose, Toast.LENGTH_LONG).show();
                } else {
                    setFlashcard();
                    if (mLearningFlashcards.isEmpty()) {
                        Toast.makeText(context, R.string.learning_by_lang_tost_empty_chose, Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
                        new ChangeActivityManager(mActivity).goToLearningCheck(mLearningFlashcards);
                    }
                }
            }
        };
    }

    private void setChosenLang() {
        if (mSpinnerFrom.getSelectedIndex() == 0 && mSpinnerOn.getSelectedIndex() == 0) {
            mLearningCategory.addAll(mCategoryRepository.getAllCategory());
        } else if (mSpinnerFrom.getSelectedIndex() == 0) {
            mLearningCategory.addAll(mCategoryRepository.getCategoryByLangOn(mSpinnerOn.getText().toString()));
        } else if (mSpinnerOn.getSelectedIndex() == 0) {
            mLearningCategory.addAll(mCategoryRepository.getCategoryByLangFrom(mSpinnerFrom.getText().toString()));
        } else {
            mLearningCategory.addAll(mCategoryRepository
                    .getCategoryByLang(mSpinnerFrom.getText().toString(),
                            mSpinnerOn.getText().toString()));
        }
    }

    private void setFlashcard() {
        for (Category cat : mLearningCategory) {
            mLearningFlashcards.addAll(mFlashcardRepository.getFlashcardsByCategoryID(cat.getId()));
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
