package eu.qm.fiszki.activity.myWords.flashcards;

import android.app.Activity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import eu.qm.fiszki.R;

/**
 * Created by mBoiler on 19.11.2016.
 */

public class SelectedFabManager {

    private FloatingActionButton mFabCancel;
    private FloatingActionButton mFabDelete;
    private FloatingActionButton mFabTransform;

    public SelectedFabManager(Activity activity) {
        mFabCancel =(FloatingActionButton) activity.findViewById(R.id.fab_cancel_flashcard);
        mFabDelete = (FloatingActionButton) activity.findViewById(R.id.fab_delete_flashcard);
        mFabTransform = (FloatingActionButton) activity.findViewById(R.id.fab_transform_flashcard);
    }

    public void showAll(){
        mFabCancel.show();
        mFabDelete.show();
        mFabTransform.show();
    }

    public void hideAll(){
        mFabCancel.hide();
        mFabDelete.hide();
        mFabTransform.hide();
    }
}
