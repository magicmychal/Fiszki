package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.CheckableKt;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;
import com.mikepenz.materialdrawer.model.interfaces.SelectableKt;

import eu.qm.fiszki.AlarmReceiver;
import eu.qm.fiszki.LocalSharedPreferences;
import eu.qm.fiszki.R;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

/**
 * Created by tm on 08.07.16.
 */
public class SwitchNotyfication extends SwitchDrawerItem {

    private Activity mActivity;
    private AlarmReceiver mAlarmReceiver;
    private LocalSharedPreferences mLocalSharedPreferences;
    private FlashcardRepository mFlashcardRepository;

    public SwitchNotyfication(final Activity activity) {
        this.mActivity = activity;
        this.mAlarmReceiver = new AlarmReceiver();
        this.mLocalSharedPreferences = new LocalSharedPreferences(activity);
        this.mFlashcardRepository = new FlashcardRepository(activity);

        NameableKt.withName(this, R.string.drawer_notyfication_switch_name);
        IconableKt.withIcon(this, R.drawable.ic_notifications_black_24dp);
        CheckableKt.withCheckable(this, false);
        SelectableKt.withSelectable(this, false);

        this.withSwitchEnabled(!mFlashcardRepository.getAllFlashcards().isEmpty());

        // Sync switch position
        CheckableKt.withChecked(this, mLocalSharedPreferences.getNotificationStatus() != 0);

        this.withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem<?> drawerItem, CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAlarmReceiver.close(activity);
                    mAlarmReceiver.start(activity);
                    mLocalSharedPreferences.setNotificationStatus(1);
                    if (mLocalSharedPreferences.getNotificationPosition() == 0) {
                        mLocalSharedPreferences.setNotificationPosition(3);
                    }
                    Toast.makeText(activity.getBaseContext(), activity.getString(R.string.drawer_notyfication_switch_toast_on), Toast.LENGTH_SHORT).show();
                } else {
                    mAlarmReceiver.close(activity);
                    mLocalSharedPreferences.setNotificationStatus(0);
                    Toast.makeText(activity.getBaseContext(), activity.getString(R.string.drawer_notyfication_switch_toast_off), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
