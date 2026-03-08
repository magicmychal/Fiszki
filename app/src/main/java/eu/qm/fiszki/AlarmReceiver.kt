package eu.qm.fiszki

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import eu.qm.fiszki.activity.CheckActivity
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "fiszki_notifications"
        private const val PENDING_INTENT_FLAGS =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        private const val REQUEST_CODE = 42

        fun scheduleNext(context: Context) {
            val prefs = LocalSharedPreferences(context)
            if (!prefs.notificationEnabled) return

            val hour = prefs.notificationHour
            val minute = prefs.notificationMinute
            val enabledDays = prefs.notificationDays

            if (enabledDays.isEmpty()) return

            val now = Calendar.getInstance()
            val alarm = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Find the next enabled day
            // Calendar days: Sun=1, Mon=2, ..., Sat=7
            // Our days: Mon=1, Tue=2, ..., Sun=7
            fun calendarDayToOurDay(calDay: Int): Int = if (calDay == Calendar.SUNDAY) 7 else calDay - 1

            fun ourDayToCalendarDay(ourDay: Int): Int = if (ourDay == 7) Calendar.SUNDAY else ourDay + 1

            // If today's time has passed, start searching from tomorrow
            if (alarm.before(now) || alarm == now) {
                alarm.add(Calendar.DAY_OF_YEAR, 1)
            }

            // Search up to 7 days ahead for an enabled day
            for (i in 0 until 7) {
                val ourDay = calendarDayToOurDay(alarm.get(Calendar.DAY_OF_WEEK))
                if (enabledDays.contains(ourDay.toString())) break
                alarm.add(Calendar.DAY_OF_YEAR, 1)
            }

            val intent = Intent(context, AlarmReceiver::class.java)
            val pi = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PENDING_INTENT_FLAGS)
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pi)
        }

        fun cancel(context: Context) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pi = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PENDING_INTENT_FLAGS)
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(pi)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = LocalSharedPreferences(context)
            if (prefs.notificationEnabled) {
                scheduleNext(context)
            }
            return
        }

        // Alarm fired — show notification, then schedule next
        createNotificationChannel(context)

        val icon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        val pi = PendingIntent.getActivity(
            context, 69,
            Intent(context, CheckActivity::class.java), PENDING_INTENT_FLAGS
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setLargeIcon(icon)
            setSmallIcon(R.mipmap.ic_stat_f)
            setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_message)))
            setContentTitle(context.getString(R.string.notification_title))
            setContentText(context.getString(R.string.notification_message))
            setContentIntent(pi)
            setAutoCancel(true)
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(100, builder.build())

        scheduleNext(context)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_title),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_message)
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}
