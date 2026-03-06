package eu.qm.fiszki.activity.learning

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomnavigation.BottomNavigationView
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.FiszkiTheme
import eu.qm.fiszki.activity.SettingsActivity
import eu.qm.fiszki.activity.exam.ExamActivity
import eu.qm.fiszki.activity.myWords.category.CategoryActivity
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class LearningActivity : AppCompatActivity() {

    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mCategoryRepository: CategoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_learning)

        mFlashcardRepository = FlashcardRepository(this)
        mCategoryRepository = CategoryRepository(this)
        buildComposeContent()
        buildBottomNav()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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
