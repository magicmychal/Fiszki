package click.quickclicker.fiszki.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper

/**
 * Transparent trampoline activity launched from the daily reminder notification.
 * Opens the main screen on the Practice tab so the user can choose what to study.
 */
class NotificationLaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OrientationHelper.lockPortraitOnPhone(this)

        val intent = Intent(this, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_learning)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }
}
