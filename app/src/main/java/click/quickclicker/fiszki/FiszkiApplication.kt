package click.quickclicker.fiszki

import android.app.Application
import com.google.android.material.color.DynamicColors
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
            options.isEnableUserInteractionTracing = true
            options.tracesSampleRate = 1.0
            options.isDebug = BuildConfig.DEBUG
        }
    }
}
