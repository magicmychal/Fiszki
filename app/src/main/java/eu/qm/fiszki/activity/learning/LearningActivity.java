package eu.qm.fiszki.activity.learning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import eu.qm.fiszki.FirebaseManager;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.ChangeActivityManager;
import eu.qm.fiszki.dialogs.learning.ByCategoryLearningDialog;
import eu.qm.fiszki.dialogs.learning.ByLanguageLearningDialog;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

public class LearningActivity extends AppCompatActivity {

    private FlashcardRepository mFlashcardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        buildToolbar();
        mFlashcardRepository = new FlashcardRepository(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void buildToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.learning_toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void onAllClick(View view) {
        new FirebaseManager(this).sendEvent(FirebaseManager.Params.LEARNING_ALL);
        new ChangeActivityManager(this).goToLearningCheck(mFlashcardRepository.getAllFlashcards());
    }

    public void onCategoryClick(View view) {
        new ByCategoryLearningDialog(this).show();
    }

    public void onLangClick(View view) {
        new ByLanguageLearningDialog(this).show();
    }
}
