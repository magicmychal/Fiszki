package eu.qm.fiszki.activity.myWords.flashcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.myWords.CategoryManagerSingleton;
import eu.qm.fiszki.activity.myWords.category.CategoryActivity;
import eu.qm.fiszki.listeners.flashcard.FlashcardAddFab;
import eu.qm.fiszki.listeners.flashcard.FlashcardCancelFab;
import eu.qm.fiszki.listeners.flashcard.FlashcardDeleteFab;
import eu.qm.fiszki.listeners.flashcard.FlashcardTransformFab;
import eu.qm.fiszki.model.category.Category;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

public class FlashcardsActivity extends AppCompatActivity {

    private Activity mActivity;
    private TextView mEmptyFlashcard;
    private RecyclerView mRecycleView;
    private Category mCurrentCategory;
    private FloatingActionButton mFabAdd;
    private SelectedFabManager mFabManager;
    private FloatingActionButton mFabDelete;
    private FloatingActionButton mFabCancel;
    private FloatingActionButton mFabTransform;
    private FlashcardRepository mFlashcardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NightModeController(this).useTheme();
        setContentView(R.layout.flashcards_activity);
        init();
        buildToolbar();
        buildListView();
        buildFabs();
    }

    private void init() {
        this.mActivity = this;
        mFabManager = new SelectedFabManager(mActivity);
        mFlashcardRepository = new FlashcardRepository(mActivity);
        mEmptyFlashcard = (TextView) mActivity.findViewById(R.id.empty_category_text);
        mFabAdd = (FloatingActionButton) mActivity.findViewById(R.id.fab_add_flashcard);
        mFabCancel = (FloatingActionButton) mActivity.findViewById(R.id.fab_cancel_flashcard);
        mFabDelete = (FloatingActionButton) mActivity.findViewById(R.id.fab_delete_flashcard);
        mFabTransform = (FloatingActionButton) mActivity.findViewById(R.id.fab_transform_flashcard);
        mCurrentCategory = new CategoryRepository(mActivity)
                .getCategoryByID(CategoryManagerSingleton.getCurrentCategoryId());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            updateListView();
            mFabManager.hideAll();
            SelectedFlashcardsSingleton.clearFlashcards();
        }
    }

    @Override
    public void onBackPressed() {
        SelectedFlashcardsSingleton.clearFlashcards();
        mFabManager.hideAll();
        mActivity.startActivity(new Intent(mActivity, CategoryActivity.class));
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }

    private void buildFabs() {
        mFabAdd.setOnClickListener(new FlashcardAddFab(mActivity));
        mFabCancel.setOnClickListener(new FlashcardCancelFab(mActivity));
        mFabDelete.setOnClickListener(new FlashcardDeleteFab(mActivity));
        mFabTransform.setOnClickListener(new FlashcardTransformFab(mActivity));
    }

    private void buildToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px);
        if (mCurrentCategory.getCategory() == null) {
            toolbar.setTitle(R.string.flashcard_toolbar_null_category);
        } else {
            toolbar.setTitle(mCurrentCategory.getCategory());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void buildListView() {
        mRecycleView = (RecyclerView) findViewById(R.id.listview_flashcard);
        StaggeredGridLayoutManager staggeredLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(staggeredLayoutManager);
    }

    private void updateListView() {
        ArrayList<Flashcard> flashcards =
                mFlashcardRepository.getFlashcardsByCategoryID(mCurrentCategory.getId());

        if (flashcards.isEmpty()) {
            mEmptyFlashcard.setVisibility(View.VISIBLE);
        } else {
            mEmptyFlashcard.setVisibility(View.INVISIBLE);
        }

        FlashcardShowAdapter adapter = new FlashcardShowAdapter(mActivity, flashcards);
        mRecycleView.swapAdapter(adapter, false);
    }
}
