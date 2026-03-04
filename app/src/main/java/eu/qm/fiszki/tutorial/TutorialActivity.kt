package eu.qm.fiszki.tutorial

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.MainActivity
import eu.qm.fiszki.tutorial.page.ColorPage

class TutorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_tutorial)

        if (savedInstanceState == null) {
            replaceTutorialFragment()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    fun replaceTutorialFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, ColorPage())
            commit()
        }
    }

    fun goButton(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun skipButton(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
