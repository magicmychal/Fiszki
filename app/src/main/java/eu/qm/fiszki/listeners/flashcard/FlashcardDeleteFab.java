package eu.qm.fiszki.listeners.flashcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import java.util.ArrayList;

import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.myWords.flashcards.SelectedFlashcardsSingleton;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;
import eu.qm.fiszki.activity.myWords.flashcards.SelectedFabManager;

/**
 * Created by mBoiler on 19.11.2016.
 */

public class FlashcardDeleteFab implements View.OnClickListener {

    private Activity mActivity;
    private ArrayList<Flashcard> mFlashcards;
    private FlashcardRepository mFlashcardRepository;
    private SelectedFabManager mFabManager;

    public FlashcardDeleteFab(Activity activity) {
        this.mActivity = activity;
        mFlashcardRepository = new FlashcardRepository(activity);
        mFabManager = new SelectedFabManager(activity);
        mFlashcards = new ArrayList<Flashcard>();
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setMessage(R.string.flashcards_delete_message)
                .setPositiveButton(R.string.button_action_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mFlashcards.addAll(SelectedFlashcardsSingleton.getFlashcards());
                        mFlashcardRepository.deleteFlashcards(mFlashcards);
                        mindfulSnackbar();
                        mFabManager.hideAll();
                        SelectedFlashcardsSingleton.clearFlashcards();
                        mActivity.onWindowFocusChanged(true);
                    }
                })
                .setNegativeButton(R.string.button_action_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).show();
    }

    private void mindfulSnackbar() {
        Snackbar snackbar = Snackbar
                .make(mActivity.getCurrentFocus(), R.string.snackbar_return_category_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_return_word_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFlashcardRepository.addFlashcards(mFlashcards);
                        mActivity.onWindowFocusChanged(true);
                    }
                });

        snackbar.show();
    }
}
