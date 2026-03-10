package eu.qm.fiszki

import android.content.Context
import android.content.SharedPreferences

class LocalSharedPreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "fiszki_prefs"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_NOTIFICATION_HOUR = "notification_hour"
        private const val KEY_NOTIFICATION_MINUTE = "notification_minute"
        private const val KEY_NOTIFICATION_DAYS = "notification_days"
        private const val KEY_COLOR_PALETTE = "color_palette"
        private const val KEY_DIAGNOSTIC_DATA = "diagnostic_data_enabled"
        private const val KEY_USE_FSRS = "use_fsrs_algorithm"

        const val PALETTE_PURPLE = 0
        const val PALETTE_YELLOW = 1

        // Old pref file names for migration
        private const val OLD_NOTIFICATION_STATUS = "notification_status"

        private val ALL_DAYS = (1..7).map { it.toString() }.toSet()
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        migrateOldPrefs(context)
    }

    var notificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, value).apply()
        }

    var notificationHour: Int
        get() = prefs.getInt(KEY_NOTIFICATION_HOUR, 9)
        set(value) {
            prefs.edit().putInt(KEY_NOTIFICATION_HOUR, value).apply()
        }

    var notificationMinute: Int
        get() = prefs.getInt(KEY_NOTIFICATION_MINUTE, 0)
        set(value) {
            prefs.edit().putInt(KEY_NOTIFICATION_MINUTE, value).apply()
        }

    var notificationDays: Set<String>
        get() = prefs.getStringSet(KEY_NOTIFICATION_DAYS, ALL_DAYS) ?: ALL_DAYS
        set(value) {
            prefs.edit().putStringSet(KEY_NOTIFICATION_DAYS, value).apply()
        }

    var colorPalette: Int
        get() = prefs.getInt(KEY_COLOR_PALETTE, PALETTE_PURPLE)
        set(value) {
            prefs.edit().putInt(KEY_COLOR_PALETTE, value).apply()
        }

    var diagnosticDataEnabled: Boolean
        get() = prefs.getBoolean(KEY_DIAGNOSTIC_DATA, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DIAGNOSTIC_DATA, value).apply()
        }

    var useFsrsAlgorithm: Boolean
        get() = prefs.getBoolean(KEY_USE_FSRS, true)
        set(value) {
            prefs.edit().putBoolean(KEY_USE_FSRS, value).apply()
        }

    private fun migrateOldPrefs(context: Context) {
        if (prefs.contains("migrated_v2")) return
        val oldStatusPrefs = context.getSharedPreferences(OLD_NOTIFICATION_STATUS, Context.MODE_PRIVATE)
        if (oldStatusPrefs.getInt(OLD_NOTIFICATION_STATUS, 0) == 1) {
            notificationEnabled = true
        }
        // Clean up old pref files
        oldStatusPrefs.edit().clear().apply()
        context.getSharedPreferences("notification_time", Context.MODE_PRIVATE).edit().clear().apply()
        prefs.edit().putBoolean("migrated_v2", true).apply()
    }
}
