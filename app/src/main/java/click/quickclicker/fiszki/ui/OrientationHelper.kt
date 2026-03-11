package click.quickclicker.fiszki.ui

import android.app.Activity
import android.content.pm.ActivityInfo

object OrientationHelper {

    fun lockPortraitOnPhone(activity: Activity) {
        val smallestWidth = activity.resources.configuration.smallestScreenWidthDp
        activity.requestedOrientation = if (smallestWidth < 600) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }
}
