package eu.qm.fiszki;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalSharedPreferences {

    private static final String NOTIFICATION_POSITION = "notification_time";
    private static final String NOTIFICATION_STATUS = "notification_status";
    private SharedPreferences mNotificationPositionPreferences;
    private SharedPreferences mNotificationStatusPreferences;

    public LocalSharedPreferences(Context context) {
        mNotificationPositionPreferences = context.getSharedPreferences(NOTIFICATION_POSITION, Context.MODE_PRIVATE);
        mNotificationStatusPreferences = context.getSharedPreferences(NOTIFICATION_STATUS, Context.MODE_PRIVATE);
    }

    public int getNotificationPosition() {
        return mNotificationPositionPreferences.getInt(NOTIFICATION_POSITION, 0);
    }

    public void setNotificationPosition(int value) {
        mNotificationPositionPreferences.edit()
                .putInt(NOTIFICATION_POSITION, value)
                .apply();
    }

    /*
     *status 1 - notification on
     *status 0 - notification off
     */
    public int getNotificationStatus() {
        return mNotificationStatusPreferences.getInt(NOTIFICATION_STATUS, 0);
    }

    public void setNotificationStatus(int value) {
        mNotificationStatusPreferences.edit()
                .putInt(NOTIFICATION_STATUS, value)
                .apply();
    }
}
