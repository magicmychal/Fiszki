package click.quickclicker.fiszki.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.database.FiszkiDatabase
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.ui.OrientationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.sentry.Sentry

class NavHostActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TAB = "tab"
    }

    private var pendingTab: NavTab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        OrientationHelper.lockPortraitOnPhone(this)

        try {
            CategoryRepository(this).addSystemCategory()
        } catch (e: Exception) {
            Sentry.captureException(e)
            showDatabaseErrorDialog(e)
            return
        }

        val initialTab = resolveTab(intent.getIntExtra(EXTRA_TAB, 0))

        setContent {
            FiszkiTheme {
                AdaptiveNavHost(initialTab = initialTab)
            }
        }
    }

    private fun showDatabaseErrorDialog(error: Exception) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.db_error_title))
            .setMessage(getString(R.string.db_error_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.db_error_reset)) { _, _ ->
                FiszkiDatabase.resetDatabase(this)
                recreate()
            }
            .setNegativeButton(getString(R.string.db_error_close)) { _, _ ->
                finishAffinity()
            }
            .show()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val tabId = intent.getIntExtra(EXTRA_TAB, -1)
        if (tabId != -1) {
            recreate()
        }
    }

    private fun resolveTab(tabId: Int): NavTab = when (tabId) {
        R.id.nav_flashcards -> NavTab.FLASHCARDS
        R.id.nav_learning -> NavTab.LEARNING
        R.id.nav_exam -> NavTab.EXAM
        R.id.nav_settings -> NavTab.SETTINGS
        else -> NavTab.FLASHCARDS
    }
}
