package eu.qm.fiszki.activity.learning;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.ChangeActivityManager;
import eu.qm.fiszki.algorithm.Algorithm;
import eu.qm.fiszki.dialogs.learning.BadAnswerLearnigDialog;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

public class LearningCheckActivity extends AppCompatActivity {

    private TextView mLang;
    private TextView mWord;
    private TextView mCategory;
    private Activity mActivity;
    private Algorithm mAlgorithm;
    private Category mDrawnCategory;
    private Flashcard mDrawnFlashcard;
    private TextInputEditText mTranslate;
    private ArrayList<Flashcard> mFlashcardsPool;
    private CategoryRepository mCategoryRepository;
    private FlashcardRepository mFlashcardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NightModeController(this).useTheme();
        setContentView(R.layout.activity_learning_check);
        init();
        buildToolbar();
        buildDoneKey();
        drawFlashcard();
    }

    @Override
    public void onBackPressed() {
        new ChangeActivityManager(mActivity).exitLearningCheck();
    }

    private void init() {
        mActivity = this;
        mAlgorithm = new Algorithm(mActivity);
        mCategoryRepository = new CategoryRepository(mActivity);
        mFlashcardRepository = new FlashcardRepository(mActivity);
        mLang = (TextView) mActivity.findViewById(R.id.learning_check_lang_text);
        mWord = (TextView) mActivity.findViewById(R.id.learning_check_word_text);
        mCategory = (TextView) mActivity.findViewById(R.id.learning_check_category_text);
        mTranslate = (TextInputEditText) mActivity.findViewById(R.id.learning_check_edit_text);
        mTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mFlashcardsPool = (ArrayList<Flashcard>) this.getIntent()
                .getSerializableExtra(ChangeActivityManager.FLASHCARDS_KEY_INTENT);
    }

    private void buildToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.learning_check_toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void buildDoneKey() {
        mTranslate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    check();
                    return true;
                }
                return false;
            }
        });
    }

    public void drawFlashcard() {
        mDrawnFlashcard = mAlgorithm.drawCardAlgorithm(mFlashcardsPool);
        mDrawnCategory = mCategoryRepository.getCategoryByID(mDrawnFlashcard.getCategoryID());
        setLangText();
        setCategoryText();
        setWordText();
        mTranslate.setText("");
    }

    private void setLangText() {
        if (mDrawnCategory.getLangFrom() == null || mDrawnCategory.getLangFrom().isEmpty() ||
                mDrawnCategory.getLangOn() == null || mDrawnCategory.getLangOn().isEmpty()) {
            mLang.setText(mActivity.getResources().getString(R.string.learning_check_lang_translate));
        } else {
            mLang.setText(mActivity.getResources().getString(R.string.learning_check_lang_translate_1) +
                    " " +
                    mDrawnCategory.getLangFrom() +
                    " " +
                    mActivity.getResources().getString(R.string.learning_check_lang_translate_2) +
                    " " +
                    mDrawnCategory.getLangOn());
        }
    }

    private void setCategoryText() {
        mCategory.setText("(" + mDrawnCategory.getCategory() + ")");
    }

    private void setWordText() {
        mWord.setText(mDrawnFlashcard.getWord());
    }

    private void check() {
        if (mTranslate.getText().toString().trim().equals(mDrawnFlashcard.getTranslation())) {
            Toast.makeText(mActivity, R.string.alert_message_pass, Toast.LENGTH_SHORT).show();
            mFlashcardRepository.upFlashcardPassStatistic(mDrawnFlashcard);
            mFlashcardRepository.upFlashcardPriority(mDrawnFlashcard);
            mTranslate.setText("");
            drawFlashcard();
        } else {
            mFlashcardRepository.upFlashcardFailStatistic(mDrawnFlashcard);
            mFlashcardRepository.downFlashcardPriority(mDrawnFlashcard);
            new BadAnswerLearnigDialog(mActivity, mDrawnFlashcard, this).show();
        }
    }

    public void skipFlashcard(View view) {
        drawFlashcard();
        Toast.makeText(this,R.string.learning_check_menu_skip_toast,Toast.LENGTH_SHORT).show();
    }


}
