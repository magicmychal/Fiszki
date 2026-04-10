package click.quickclicker.fiszki.activity.exam

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.ui.TabletContentWrapper

class ExamCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        val totalRounds = intent.getIntExtra(ChangeActivityManager.EXAM_ROUNDS_KEY_INTENT, 10)
        val categoryName = intent.getStringExtra(ChangeActivityManager.EXAM_CATEGORY_NAME_KEY_INTENT)
        val languagePair = intent.getStringExtra(ChangeActivityManager.EXAM_LANGUAGE_PAIR_KEY_INTENT)

        val repo = FlashcardRepository(this)
        val flashcardIds = intent.getIntArrayExtra(ChangeActivityManager.FLASHCARD_IDS_KEY_INTENT)
        val flashcardsPool = if (flashcardIds != null) {
            repo.getFlashcardsByIds(flashcardIds.toList())
        } else {
            val categoryId = intent.getIntExtra(
                ChangeActivityManager.CATEGORY_ID_KEY_INTENT,
                ChangeActivityManager.ALL_CATEGORIES
            )
            if (categoryId == ChangeActivityManager.ALL_CATEGORIES) {
                repo.getAllFlashcards()
            } else {
                repo.getFlashcardsByCategoryID(categoryId)
            }
        }

        if (flashcardsPool.isEmpty()) {
            finish()
            return
        }

        setContent {
            FiszkiTheme {
                TabletContentWrapper {
                    ExamCheckScreen(
                        flashcardsPool = flashcardsPool,
                        totalRounds = totalRounds,
                        categoryName = categoryName,
                        languagePair = languagePair,
                        onBack = {
                            ChangeActivityManager(this@ExamCheckActivity).exitExamCheck()
                        },
                        onShowSummary = { summaryData ->
                            ChangeActivityManager(this@ExamCheckActivity).goToExamSummary(summaryData)
                        }
                    )
                }
            }
        }
    }
}
