package click.quickclicker.fiszki.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper

class AlgorithmInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        setContent {
            FiszkiTheme {
                AlgorithmInfoScreen(onBack = { finish() })
            }
        }
    }
}
