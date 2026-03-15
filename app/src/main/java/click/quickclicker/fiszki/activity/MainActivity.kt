package click.quickclicker.fiszki.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import click.quickclicker.fiszki.Alert
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.dialogs.flashcard.QuicklyAddFlashcardDialog
import click.quickclicker.fiszki.drawer.DrawerMain
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mSlider: MaterialDrawerSliderView
    private lateinit var mToolbar: Toolbar
    private lateinit var mActivity: Activity
    private var mCountBackPress = 0
    private lateinit var mFab: FloatingActionButton
    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardRepository: FlashcardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.activity_main)

        init()
        handleWindowInsets()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                    mCountBackPress = 0
                } else {
                    if (mCountBackPress == 0) {
                        Toast.makeText(mActivity, R.string.back_press_toast, Toast.LENGTH_SHORT).show()
                        mCountBackPress++
                    } else {
                        finish()
                    }
                }
            }
        })
        buildDrawer()
        buildFAB()
        buildToolbar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            buildDrawer()
        }
    }

    override fun onStart() {
        super.onStart()
        mCategoryRepository.addSystemCategory()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun init() {
        mActivity = this
        mCountBackPress = 0
        mCategoryRepository = CategoryRepository(mActivity)
        mFlashcardRepository = FlashcardRepository(mActivity)
    }

    private fun handleWindowInsets() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_flashcard)
        val lp = fab.layoutParams as android.view.ViewGroup.MarginLayoutParams
        val originalMargins = Margins(lp.leftMargin, lp.bottomMargin, lp.rightMargin)
        ViewCompat.setOnApplyWindowInsetsListener(fab) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<android.view.ViewGroup.MarginLayoutParams> {
                leftMargin = originalMargins.left + bars.left
                bottomMargin = originalMargins.bottom + bars.bottom
                rightMargin = originalMargins.right + bars.right
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private data class Margins(val left: Int, val bottom: Int, val right: Int)

    private fun buildDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mSlider = findViewById(R.id.drawer_slider)
        mSlider.onDrawerItemClickListener = { _, _, _ ->
            mSlider.setSelection(-1, false)
            false
        }
        DrawerMain(mActivity).setup(mSlider)
    }

    private fun buildFAB() {
        mFab = findViewById(R.id.fab_add_flashcard)
        mFab.setOnClickListener {
            QuicklyAddFlashcardDialog(mActivity).show()
        }
    }

    private fun buildToolbar() {
        mToolbar = findViewById(R.id.toolbar)
        mToolbar.title = resources.getString(R.string.app_name)
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_36px)
        mToolbar.setNavigationOnClickListener {
            mDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun myWordsCardClick(view: View) {
        startActivity(Intent(this, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_flashcards)
        })
    }

    fun learningCardClick(view: View) {
        if (mFlashcardRepository.countFlashcards() == 0) {
            Alert().addFiszkiToFeature(mActivity).show()
        } else {
            startActivity(Intent(this, NavHostActivity::class.java).apply {
                putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_learning)
            })
        }
    }

    fun examCardClick(view: View) {
        if (mFlashcardRepository.countFlashcards() == 0) {
            Alert().addFiszkiToFeature(mActivity).show()
        } else {
            startActivity(Intent(this, NavHostActivity::class.java).apply {
                putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_exam)
            })
        }
    }
}
