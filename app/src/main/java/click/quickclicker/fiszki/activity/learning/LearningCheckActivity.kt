package click.quickclicker.fiszki.activity.learning

import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.ui.TabletContentWrapper

class LearningCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        OrientationHelper.lockPortraitOnPhone(this)

        @Suppress("UNCHECKED_CAST")
        val flashcardsPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(ChangeActivityManager.FLASHCARDS_KEY_INTENT, ArrayList::class.java)
                as ArrayList<Flashcard>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(ChangeActivityManager.FLASHCARDS_KEY_INTENT)
                as ArrayList<Flashcard>
        }
        val strictMode = intent.getBooleanExtra(ChangeActivityManager.STRICT_MODE_KEY_INTENT, true)
        val reversed = intent.getBooleanExtra(ChangeActivityManager.REVERSED_KEY_INTENT, false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ChangeActivityManager(this@LearningCheckActivity).exitLearningCheck()
            }
        })

        setContent {
            FiszkiTheme {
                TabletContentWrapper {
                    LearningCheckScreen(
                        flashcardsPool = flashcardsPool,
                        strictMode = strictMode,
                        reversed = reversed,
                        onFinish = { onBackPressedDispatcher.onBackPressed() }
                    )
                }
            }
        }
    }
}
