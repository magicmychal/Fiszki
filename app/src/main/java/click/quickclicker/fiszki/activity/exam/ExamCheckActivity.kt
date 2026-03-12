package click.quickclicker.fiszki.activity.exam

import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.dialogs.exam.EndExamDialog
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.ui.TabletContentWrapper

class ExamCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        enableEdgeToEdge()
        OrientationHelper.lockPortraitOnPhone(this)

        @Suppress("UNCHECKED_CAST")
        val extras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(ChangeActivityManager.EXAM_REPEAT_KEY_INTENT, ArrayList::class.java) as ArrayList<*>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(ChangeActivityManager.EXAM_REPEAT_KEY_INTENT) as ArrayList<*>
        }
        @Suppress("UNCHECKED_CAST")
        val flashcardsPool = extras[0] as ArrayList<Flashcard>
        val totalRounds = extras[1] as Int
        val categoryName = if (extras.size > 2) extras[2] as? String else null
        val languagePair = if (extras.size > 3) extras[3] as? String else null

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
                            EndExamDialog(
                                this@ExamCheckActivity,
                                summaryData,
                                ArrayList<Flashcard>()
                            ).show()
                        }
                    )
                }
            }
        }
    }
}
