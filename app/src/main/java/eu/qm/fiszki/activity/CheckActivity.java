package eu.qm.fiszki.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import eu.qm.fiszki.FirebaseManager;
import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.algorithm.Algorithm;
import eu.qm.fiszki.algorithm.CatcherFlashcardToAlgorithm;
import eu.qm.fiszki.dialogs.check.EmptyDBCheckDialog;
import eu.qm.fiszki.dialogs.check.EmptySelectedCheckDialog;
import eu.qm.fiszki.dialogs.check.FailCheckDialog;
import eu.qm.fiszki.dialogs.check.PassCheckDialog;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

public class CheckActivity extends AppCompatActivity {

    private Activity mActivity;
    private Algorithm mAlgorithm;
    private CategoryRepository mCategoryRepository;
    private FlashcardRepository mFlashcardRepository;
    private TextView mLang;
    private TextView mWord;
    private TextView mCategory;
    private TextInputEditText mTranslate;
    private Flashcard mDrawnFlashcard;
    private Category mDrawnCategory;
    private ArrayList<Flashcard> mPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NightModeController(this).useTheme();
        setContentView(R.layout.activity_check);
        setToolbar();
        init();
        buildDoneKey();
        drawFlashcard();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.check_title);
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        mActivity = this;
        mAlgorithm = new Algorithm(mActivity);
        mCategoryRepository = new CategoryRepository(mActivity);
        mFlashcardRepository = new FlashcardRepository(mActivity);
        mLang = (TextView) mActivity.findViewById(R.id.check_lang_text);
        mWord = (TextView) mActivity.findViewById(R.id.check_word_text);
        mCategory = (TextView) mActivity.findViewById(R.id.check_category_text);
        mTranslate = (TextInputEditText) mActivity.findViewById(R.id.check_edit_text);
        mTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPool = new CatcherFlashcardToAlgorithm(mActivity).getFlashcardsFromChosenCategoryToNotification();
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
        if(isCondition()) {
            mDrawnFlashcard = mAlgorithm.drawCardAlgorithm(mPool);
            mDrawnCategory = mCategoryRepository.getCategoryByID(mDrawnFlashcard.getCategoryID());
            setLangText();
            setCategoryText();
            setWordText();
            mTranslate.setText("");
        }
    }

    private boolean isCondition() {
        if(mFlashcardRepository.getAllFlashcards().isEmpty()){
            new EmptyDBCheckDialog(this).show();
            return false;
        }else if(mPool.isEmpty()){
            new EmptySelectedCheckDialog(this).show();
            mPool = mFlashcardRepository.getAllFlashcards();
        }
        return true;
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
            mFlashcardRepository.upFlashcardPassStatistic(mDrawnFlashcard);
            mFlashcardRepository.upFlashcardPriority(mDrawnFlashcard);
            new FirebaseManager(mActivity).sendEvent(FirebaseManager.Params.NOTYFI_PASS);
            new PassCheckDialog(this).show();
        } else {
            mFlashcardRepository.upFlashcardFailStatistic(mDrawnFlashcard);
            mFlashcardRepository.downFlashcardPriority(mDrawnFlashcard);
            new FirebaseManager(mActivity).sendEvent(FirebaseManager.Params.NOTYFI_WRONG);
            new FailCheckDialog(this,mDrawnFlashcard).show();
        }
    }


}
