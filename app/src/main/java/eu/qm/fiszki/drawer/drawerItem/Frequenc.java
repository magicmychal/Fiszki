package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import eu.qm.fiszki.AlarmReceiver;
import eu.qm.fiszki.LocalSharedPreferences;
import eu.qm.fiszki.R;

/**
 * Created by Siusiacz on 09.07.2016.
 */
public class Frequenc extends PrimaryDrawerItem {

    private LocalSharedPreferences localSharedPreferences;
    private AlarmReceiver alarmReceiver;

    public Frequenc(final Activity activity) {
        localSharedPreferences = new LocalSharedPreferences(activity);
        alarmReceiver = new AlarmReceiver();

        NameableKt.withName(this, R.string.drawer_freqenc_name);
        IconableKt.withIcon(this, R.drawable.clock_alert);
        this.withOnDrawerItemClickListener((view, drawerItem, position) -> {
            new MaterialDialog.Builder(activity)
                    .title(R.string.drawer_freqenc_name)
                    .items(R.array.notification_frequency)
                    .itemsCallbackSingleChoice(getSelectedFreq(), new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            RBClick(which + 1, activity);
                            return false;
                        }
                    })
                    .positiveText(R.string.button_action_ok)
                    .cancelable(false)
                    .show();
            return false;
        });
    }

    private void RBClick(int id, Activity activity) {
        alarmReceiver.close(activity);
        localSharedPreferences.setNotificationPosition(id);
        if (localSharedPreferences.getNotificationStatus() == 1) {
            alarmReceiver.start(activity);
        }
    }

    private int getSelectedFreq() {
        switch (localSharedPreferences.getNotificationPosition()) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            default:
                return -1;
        }
    }
}
