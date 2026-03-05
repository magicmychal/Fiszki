package eu.qm.fiszki

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticFeedback {

    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun vibrateCorrect(context: Context) {
        val vibrator = getVibrator(context)
        vibrator.vibrate(VibrationEffect.createOneShot(30, 40))
    }

    fun vibrateWrong(context: Context) {
        val vibrator = getVibrator(context)
        vibrator.vibrate(VibrationEffect.createOneShot(200, 200))
    }
}
