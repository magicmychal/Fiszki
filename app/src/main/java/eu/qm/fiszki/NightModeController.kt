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
        if (getStatus() == 1) {
            activity.setTheme(R.style.NightMode)
        } else {
            activity.setTheme(R.style.AppTheme)
        }
    }
}
