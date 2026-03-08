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
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
    }

    fun vibrateWrong(context: Context) {
        val vibrator = getVibrator(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            vibrator.arePrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_THUD)
        ) {
            val composition = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.7f, 400)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.5f, 400)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.3f, 400)
                .compose()
            vibrator.vibrate(composition)
        } else {
            // Fallback: waveform pattern over ~2 seconds
            // Pattern: wait, vibrate, pause, vibrate, pause, vibrate, pause, vibrate
            val timings = longArrayOf(0, 150, 250, 150, 250, 150, 250, 100)
            val amplitudes = intArrayOf(0, 255, 0, 180, 0, 130, 0, 80)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        }
    }
}
