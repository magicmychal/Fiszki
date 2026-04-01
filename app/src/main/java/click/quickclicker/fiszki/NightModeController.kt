package click.quickclicker.fiszki

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

class NightModeController(private val activity: Activity) {

    companion object {
        private const val NIGHTMODE_STATUS = "nightmode_status"
    }

    private val prefs = activity.getSharedPreferences(NIGHTMODE_STATUS, Context.MODE_PRIVATE)

    fun on() {
        prefs.edit().putInt(NIGHTMODE_STATUS, 1).apply()
    }

    fun off() {
        prefs.edit().putInt(NIGHTMODE_STATUS, 0).apply()
    }

    fun getStatus(): Int = prefs.getInt(NIGHTMODE_STATUS, 0)

    fun isDarkMode(): Boolean = getStatus() == 1

    fun useTheme() {
        val palette = LocalSharedPreferences(activity).colorPalette
        val isYellow = palette == LocalSharedPreferences.PALETTE_YELLOW
        if (isDarkMode()) {
            activity.setTheme(if (isYellow) R.style.NightMode_Yellow else R.style.NightMode)
        } else {
            activity.setTheme(if (isYellow) R.style.AppTheme_Yellow else R.style.AppTheme)
        }
        enableEdgeToEdge()
    }

    private fun enableEdgeToEdge() {
        val componentActivity = activity as? ComponentActivity ?: return
        if (isDarkMode()) {
            val darkStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            componentActivity.enableEdgeToEdge(
                statusBarStyle = darkStyle,
                navigationBarStyle = darkStyle
            )
        } else {
            val lightStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
            componentActivity.enableEdgeToEdge(
                statusBarStyle = lightStyle,
                navigationBarStyle = lightStyle
            )
        }
    }
}
