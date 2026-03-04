package eu.qm.fiszki.dialogs.flashcard;

import android.app.Activity;
import android.app.Dialog;
import androidx.annotation.NonNull;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputEditText;

import eu.qm.fiszki.R;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;
import eu.qm.fiszki.model.flashcard.ValidationFlashcards;

/**
 * Created by mBoiler on 12.11.2016.
 */

//todo pokazywanie klawiatury po fokusie

public class QuicklyAddFlashcardDialog extends MaterialDialog.Builder {

    private ValidationFlashcards mValidationFlashcards;
    private TextInputEditText mFlashcardWordET;
    private TextInputEditText mFlashcardTranslationET;
    private FlashcardRepository mFlashcardRepository;
    private Activity mActivity;

    public QuicklyAddFlashcardDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.title(R.string.add_fast_new_flashcard);
        this.customView(R.layout.flashcard_add_dialog, false);
        this.positiveText(R.string.add_new_flashcard_btn);
        this.positiveColor(mActivity.getResources().getColor(R.color.ColorPrimaryDark));
        this.onPositive(addQuicklyFlashcardBtn());
        init();
        autoDismiss(false);
        keyboardAction();

    }

    private void init() {
        mFlashcardWordET = (TextInputEditText) customView.findViewById(R.id.add_flashcard_et_word);
        mFlashcardTranslationET = (TextInputEditText) customView.findViewById(R.id.add_flashcard_et_translation);
        mFlashcardRepository = new FlashcardRepository(context);
        mValidationFlashcards = new ValidationFlashcards(context);
    }

    private void keyboardAction() {
        mFlashcardTranslationET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (i == EditorInfo.IME_ACTION_DONE)) {
                    addFlashcard();
                }
                return false;
            }
        });
    }

    private MaterialDialog.SingleButtonCallback addQuicklyFlashcardBtn() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                addFlashcard();
            }
        };
    }

    private void addFlashcard() {
        Flashcard flashcard = new Flashcard();
        flashcard.setWord(mFlashcardWordET.getText().toString().trim());
        flashcard.setTranslation(mFlashcardTranslationET.getText().toString().trim());
        flashcard.setPriority(1);
        flashcard.setCategoryID(1);

        if (mValidationFlashcards.validateAdd(flashcard)) {
            mFlashcardRepository.addFlashcard(flashcard);
            Toast.makeText(context, R.string.add_new_flashcard_toast, Toast.LENGTH_SHORT).show();
            mFlashcardWordET.setText("");
            mFlashcardTranslationET.setText("");
            mFlashcardWordET.requestFocus();
        }
    }

}
