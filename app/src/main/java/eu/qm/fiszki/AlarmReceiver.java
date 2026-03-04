package eu.qm.fiszki;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import eu.qm.fiszki.activity.CheckActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "fiszki_notifications";
    private static final int PENDING_INTENT_FLAGS =
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        long[] vibrate = new long[]{0, 100, 0, 100};
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        PendingIntent pi = PendingIntent.getActivity(context, 69, new Intent(context,
                CheckActivity.class), PENDING_INTENT_FLAGS);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setLargeIcon(icon);
        mBuilder.setSmallIcon(R.mipmap.ic_stat_f);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_message)));
        mBuilder.setContentTitle(context.getString(R.string.notification_title));
        mBuilder.setContentText(context.getString(R.string.notification_message));
        mBuilder.setContentIntent(pi);
        mBuilder.setAutoCancel(true);
        mBuilder.setVibrate(vibrate);
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(100, mBuilder.build());
    }

    public void start(Activity activity) {
        Intent alarmIntent = new Intent(activity.getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getBaseContext(), 0, alarmIntent, PENDING_INTENT_FLAGS);
        AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        long time = (1000 * 60 * timeSetter(activity));
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, time, pendingIntent);
    }

    public void close(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PENDING_INTENT_FLAGS);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_title),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.notification_message));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 100, 0, 100});
            NotificationManager nm =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
        }
    }

    private int timeSetter(Activity activity){
        LocalSharedPreferences localSharedPreferences= new LocalSharedPreferences(activity);
        if (localSharedPreferences.getNotificationPosition()==1){
            return 1;
        }else if(localSharedPreferences.getNotificationPosition()==2){
            return 5;
        }else if(localSharedPreferences.getNotificationPosition()==3){
            return 15;
        }else if(localSharedPreferences.getNotificationPosition()==4){
            return 30;
        }else{
            return 60;
        }
    }
}
