package eu.qm.fiszki.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.qm.fiszki.R
import eu.qm.fiszki.tutorial.TutorialActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val runCheck = getSharedPreferences("hasRunBefore", Context.MODE_PRIVATE)
        val hasRun = runCheck.getBoolean("hasRun", false)
        if (!hasRun) {
            runCheck.edit().putBoolean("hasRun", true).apply()
            startActivity(Intent(this, TutorialActivity::class.java))
        } else {
            startActivity(Intent(this, NavHostActivity::class.java))
        }
        finish()
    }
}
