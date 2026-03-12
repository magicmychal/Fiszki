package click.quickclicker.fiszki.activity.learning

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomnavigation.BottomNavigationView
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.SettingsActivity
import click.quickclicker.fiszki.activity.exam.ExamActivity
import click.quickclicker.fiszki.activity.myWords.category.CategoryActivity
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class LearningActivity : AppCompatActivity() {

    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mCategoryRepository: CategoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.activity_learning)

        mFlashcardRepository = FlashcardRepository(this)
        mCategoryRepository = CategoryRepository(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        buildComposeContent()
        buildBottomNav()
    }

    private fun buildComposeContent() {
        val composeView = findViewById<ComposeView>(R.id.compose_view)

        val allCategories = mCategoryRepository.getAllCategory()
        val categoryItems = buildList {
            add(
                PracticeCategoryItem(
                    id = null,
                    displayName = getString(R.string.learning_category_all),
                    langFrom = null,
                    langOn = null
                )
            )
            allCategories.forEach { cat ->
                add(
                    PracticeCategoryItem(
                        id = cat.id,
                        displayName = cat.getCategory(),
                        langFrom = cat.getLangFrom(),
                        langOn = cat.getLangOn()
                    )
                )
            }
        }

        composeView.setContent {
            FiszkiTheme {
                PracticeSetupScreen(
                    title = getString(R.string.learning_title),
                    categories = categoryItems,
                    modifier = Modifier.systemBarsPadding(),
                    onStartPractice = { strictMode, categoryId, reversed ->
                        val flashcards = if (categoryId == null) {
                            mFlashcardRepository.getAllFlashcards()
                        } else {
                            mFlashcardRepository.getFlashcardsByCategoryID(categoryId)
                        }
                        if (flashcards.isEmpty()) {
                            Toast.makeText(this, R.string.learning_no_flashcards, Toast.LENGTH_LONG).show()
                        } else {
                            ChangeActivityManager(this).goToLearningCheck(
                                flashcards = flashcards,
                                strictMode = strictMode,
                                reversed = reversed
                            )
                        }
                    }
                )
            }
        }
    }

    private fun buildBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_learning

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_learning -> true
                R.id.nav_flashcards -> {
                    startActivity(Intent(this, CategoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_exam -> {
                    startActivity(Intent(this, ExamActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
