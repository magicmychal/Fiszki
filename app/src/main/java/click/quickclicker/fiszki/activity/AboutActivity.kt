package click.quickclicker.fiszki.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.ui.OrientationHelper

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        setContent {
            FiszkiTheme {
                AboutScreen(onBack = { onBackPressedDispatcher.onBackPressed() })
            }
        }
    }
}
