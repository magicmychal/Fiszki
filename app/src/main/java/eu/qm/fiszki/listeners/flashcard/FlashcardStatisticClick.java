package eu.qm.fiszki.listeners.flashcard;

import android.app.Activity;
import android.view.View;

import eu.qm.fiszki.dialogs.flashcard.StatisticFlashcardDialog;
import eu.qm.fiszki.model.flashcard.Flashcard;

/**
 * Created by mBoiler on 22.12.2016.
 */
public class FlashcardStatisticClick implements View.OnClickListener {

    private Activity mActivity;
    private Flashcard mFlashcard;

    public FlashcardStatisticClick(Activity activity, Flashcard flashcard) {
        this.mActivity = activity;
        this.mFlashcard = flashcard;
    }

    @Override
    public void onClick(View view) {
        new StatisticFlashcardDialog(mActivity,mFlashcard).show();
    }
}
