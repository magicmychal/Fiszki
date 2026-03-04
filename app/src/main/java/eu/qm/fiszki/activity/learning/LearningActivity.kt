package eu.qm.fiszki.activity.learning

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomnavigation.BottomNavigationView
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.exam.ExamActivity
import eu.qm.fiszki.activity.myWords.category.CategoryActivity
import eu.qm.fiszki.dialogs.learning.ByCategoryLearningDialog
import eu.qm.fiszki.dialogs.learning.ByLanguageLearningDialog
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class LearningActivity : AppCompatActivity() {

    private lateinit var mFlashcardRepository: FlashcardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_learning)

        mFlashcardRepository = FlashcardRepository(this)
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

        val shapes = listOf(
            ShapeItem(
                label = getString(R.string.learning_try_all),
                color = Color(0xFF6750A4), // colorPrimaryNew
                shapeType = ShapeType.BLOB,
                onClick = {
                    ChangeActivityManager(this).goToLearningCheck(mFlashcardRepository.getAllFlashcards())
                }
            ),
            ShapeItem(
                label = getString(R.string.learning_by_language),
                color = Color(0xFF625B71), // colorSecondaryNew
                shapeType = ShapeType.ARROW,
                onClick = {
                    ByLanguageLearningDialog(this).show()
                }
            ),
            ShapeItem(
                label = getString(R.string.learning_by_category),
                color = Color(0xFF7D5260), // tertiary
                shapeType = ShapeType.FLOWER,
                onClick = {
                    ByCategoryLearningDialog(this).show()
                }
            ),
            ShapeItem(
                label = getString(R.string.learning_chat),
                color = Color(0xFFD0BCFF), // primaryContainer
                shapeType = ShapeType.HEART,
                onClick = {
                    ChangeActivityManager(this).goToChatMode(mFlashcardRepository.getAllFlashcards())
                }
            )
        )

        composeView.setContent {
            LearningScreen(
                title = getString(R.string.learning_title),
                shapes = shapes
            )
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
                else -> false
            }
        }
    }
}
