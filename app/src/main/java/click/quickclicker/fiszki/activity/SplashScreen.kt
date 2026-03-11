package click.quickclicker.fiszki.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.splash_screen)
        startActivity(Intent(this, NavHostActivity::class.java))
        finish()
    }
}
