package eu.qm.fiszki

import android.app.Application
import android.os.Build
import android.util.DisplayMetrics
import com.google.android.material.color.DynamicColors
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid

class FiszkiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        val prefs = LocalSharedPreferences(this)
        if (prefs.diagnosticDataEnabled) {
            initSentry()
        }
    }

    fun initSentry() {
        SentryAndroid.init(this) { options: SentryOptions ->
            options.dsn = "https://a0d094ed10c25e4f03d53da142e6f199@o4511009311948800.ingest.de.sentry.io/4511009313718352"
            options.isEnableUserInteractionBreadcrumbs = true
            options.tracesSampleRate = 1.0
            options.isDebug = BuildConfig.DEBUG
        }
        sendDeviceInfo()
    }

    private fun sendDeviceInfo() {
        val metrics: DisplayMetrics = resources.displayMetrics
        val resolution = "${metrics.widthPixels}x${metrics.heightPixels}"
        val phoneLang = java.util.Locale.getDefault().toLanguageTag()
        val appLang = resources.configuration.locales[0].toLanguageTag()

        Sentry.captureMessage("Someone is up!") { scope ->
            scope.level = SentryLevel.INFO
            scope.setTag("device.os", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            scope.setTag("device.model", "${Build.MANUFACTURER} ${Build.MODEL}")
            scope.setTag("device.resolution", resolution)
            scope.setTag("device.language", phoneLang)
            scope.setTag("app.language", appLang)
        }
    }
}
