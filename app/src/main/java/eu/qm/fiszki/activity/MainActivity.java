package eu.qm.fiszki.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;

import eu.qm.fiszki.Alert;
import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.exam.ExamActivity;
import eu.qm.fiszki.activity.learning.LearningActivity;
import eu.qm.fiszki.activity.myWords.category.CategoryActivity;
import eu.qm.fiszki.dialogs.flashcard.QuicklyAddFlashcardDialog;
import eu.qm.fiszki.drawer.DrawerMain;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private MaterialDrawerSliderView mSlider;
    private Toolbar mToolbar;
    private Activity mActivity;
    private int mCountBackPress;
    private FloatingActionButton mFab;
    private CategoryRepository mCategoryRepository;
    private FlashcardRepository mFlashcardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NightModeController(this).useTheme();
        setContentView(R.layout.activity_main);

        init();
        buildDrawer();
        buildFAB();
        buildToolbar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            buildDrawer();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mCountBackPress = 0;
        } else {
            if (mCountBackPress == 0) {
                Toast.makeText(mActivity,
                        R.string.back_press_toast, Toast.LENGTH_SHORT).show();
                mCountBackPress++;
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCategoryRepository.addSystemCategory();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        mActivity = this;
        mCountBackPress = 0;
        mCategoryRepository = new CategoryRepository(mActivity);
        mFlashcardRepository = new FlashcardRepository(mActivity);
    }

    private void buildDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mSlider = findViewById(R.id.drawer_slider);
        mSlider.setOnDrawerItemClickListener((view, item, position) -> {
            mSlider.setSelection(-1, false);
            return false;
        });
        new DrawerMain(mActivity).setup(mSlider);
    }

    private void buildFAB() {
        mFab = (FloatingActionButton) findViewById(R.id.fab_add_flashcard);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new QuicklyAddFlashcardDialog(mActivity).show();
            }
        });
    }

    private void buildToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_36px);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void myWordsCardClick(View view) {
        mActivity.startActivity(new Intent(this, CategoryActivity.class));
    }

    public void learningCardClick(View view) {
        if (mFlashcardRepository.countFlashcards() == 0) {
            new Alert().addFiszkiToFeature(mActivity).show();
        } else {
            startActivity(new Intent(this, LearningActivity.class));
        }
    }

    public void examCardClick(View view) {
        if (mFlashcardRepository.countFlashcards() == 0) {
            new Alert().addFiszkiToFeature(mActivity).show();
        } else {
            startActivity(new Intent(this, ExamActivity.class));
        }
    }
}
