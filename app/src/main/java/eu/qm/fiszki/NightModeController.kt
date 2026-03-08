package eu.qm.fiszki

import android.app.Activity
import android.content.Context

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

    fun useTheme() {
        val palette = LocalSharedPreferences(activity).colorPalette
        val isYellow = palette == LocalSharedPreferences.PALETTE_YELLOW
        if (getStatus() == 1) {
            activity.setTheme(if (isYellow) R.style.NightMode_Yellow else R.style.NightMode)
        } else {
            activity.setTheme(if (isYellow) R.style.AppTheme_Yellow else R.style.AppTheme)
        }
    }
}
