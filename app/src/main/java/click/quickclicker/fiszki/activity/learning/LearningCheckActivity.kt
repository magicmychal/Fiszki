package click.quickclicker.fiszki.activity.learning

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.ui.TabletContentWrapper

class LearningCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        val strictMode = intent.getBooleanExtra(ChangeActivityManager.STRICT_MODE_KEY_INTENT, true)
        val reversed = intent.getBooleanExtra(ChangeActivityManager.REVERSED_KEY_INTENT, false)

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
                    LearningCheckScreen(
                        flashcardsPool = flashcardsPool,
                        strictMode = strictMode,
                        reversed = reversed,
                        onFinish = { ChangeActivityManager(this@LearningCheckActivity).exitLearningCheck() }
                    )
                }
            }
        }
    }
}
