package eu.qm.fiszki.activity.exam;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.ChangeActivityManager;
import eu.qm.fiszki.algorithm.Algorithm;
import eu.qm.fiszki.dialogs.exam.EndExamDialog;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.Flashcard;

public class ExamCheckActivity extends AppCompatActivity {

    private TextView mWord;
    private TextView mLang;
    private int mCuntRepeat;
    private int mNuberOfRepeat;
    private Activity mActivity;
    private ArrayList<ArrayList> mBadAnswer;
    private TextView mRepreatCunter;
    private Category mDrawnCategory;
    private Flashcard mDrawnFlashcard;
    private TextInputEditText mTranslate;
    private ArrayList<Flashcard> mGoodAnswer;
    private ArrayList<Flashcard> mFlashcardPools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NightModeController(this).useTheme();
        setContentView(R.layout.activity_exam_check);

        init();
        buildToolbar();
        buildDoneBtn();
        drawFlashcard();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.exam_check_exit_question)
                .setPositiveButton(R.string.button_action_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new ChangeActivityManager(mActivity).exitExamCheck();
                    }
                })
                .setNegativeButton(R.string.button_action_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).show();
    }

    private void init() {
        this.mActivity = this;
        this.mGoodAnswer = new ArrayList<>();
        this.mBadAnswer = new ArrayList<>();
        this.mFlashcardPools = (ArrayList<Flashcard>) ((ArrayList) mActivity.getIntent()
                .getSerializableExtra(ChangeActivityManager.EXAM_REPEAT_KEY_INTENT)).get(0);
        this.mNuberOfRepeat = (int) ((ArrayList) mActivity.getIntent()
                .getSerializableExtra(ChangeActivityManager.EXAM_REPEAT_KEY_INTENT)).get(1);
        this.mRepreatCunter = (TextView) mActivity.findViewById(R.id.exam_check_cunt_repeat);
        this.mLang = (TextView) mActivity.findViewById(R.id.exam_check_lang);
        this.mWord = (TextView) mActivity.findViewById(R.id.exam_check_word);
        this.mTranslate = (TextInputEditText) mActivity.findViewById(R.id.exam_check_edit_text);
        this.mTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    private void buildToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.exam_check_toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void buildDoneBtn() {
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

    private void drawFlashcard() {
        mCuntRepeat++;
        mDrawnFlashcard = new Algorithm(mActivity).drawCardAlgorithm(mFlashcardPools);
        mDrawnCategory = new CategoryRepository(mActivity)
                .getCategoryByID(mDrawnFlashcard.getCategoryID());
        setRepeatCunter();
        setLangText();
        setWord();
        mTranslate.setText("");
    }


    private void setRepeatCunter() {
        mRepreatCunter.setText(
                mActivity.getResources().getString(R.string.exam_check_repeat_qustion_1) +
                        " " + mCuntRepeat + " " +
                        mActivity.getResources().getString(R.string.exam_check_repeat_qustion_2) +
                        " " + mNuberOfRepeat);
    }

    private void setLangText() {
        if (mDrawnCategory.getLangFrom() == null || mDrawnCategory.getLangFrom().isEmpty() ||
                mDrawnCategory.getLangOn() == null || mDrawnCategory.getLangOn().isEmpty()) {
            mLang.setText("(" + mActivity.getResources().getString(R.string.category_no_lang) + ")");
        } else {
            mLang.setText("(" + mDrawnCategory.getLangFrom() + "->" + mDrawnCategory.getLangOn() + ")");
        }
    }

    private void setWord() {
        mWord.setText(mDrawnFlashcard.getWord());
    }

    private void check() {
        if (mTranslate.getText().toString().equals(mDrawnFlashcard.getTranslation())) {
            mGoodAnswer.add(mDrawnFlashcard);
        } else {
            ArrayList badAnswer = new ArrayList();
            badAnswer.add(mDrawnFlashcard);
            badAnswer.add(mTranslate.getText().toString());
            mBadAnswer.add(badAnswer);
        }
        if (mCuntRepeat == mNuberOfRepeat) {
            new EndExamDialog(mActivity, mBadAnswer, mGoodAnswer).show();
        } else {
            drawFlashcard();
        }
    }

}
