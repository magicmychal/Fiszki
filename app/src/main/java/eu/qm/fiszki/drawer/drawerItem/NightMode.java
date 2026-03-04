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

import eu.qm.fiszki.LocalSharedPreferences;
import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.ChangeActivityManager;

/**
 * Created by Siusiacz on 09.07.2016.
 */
public class NightMode extends SwitchDrawerItem {

    private NightModeController nightModeController;

    public NightMode(final Activity activity) {
        nightModeController = new NightModeController(activity);

        NameableKt.withName(this, R.string.drawer_nightmode);
        IconableKt.withIcon(this, R.drawable.ic_weather_night);
        CheckableKt.withCheckable(this, false);
        SelectableKt.withSelectable(this, false);

        // Sync switch position
        CheckableKt.withChecked(this, nightModeController.getStatus() != 0);

        this.withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem<?> drawerItem, CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(activity, activity.getString(R.string.drawer_nightmode_toast_on), Toast.LENGTH_SHORT).show();
                    nightModeController.on();
                    new ChangeActivityManager(activity).resetMain();
                } else {
                    Toast.makeText(activity, activity.getString(R.string.drawer_nightmode_toast_off), Toast.LENGTH_SHORT).show();
                    nightModeController.off();
                    new ChangeActivityManager(activity).resetMain();
                }
            }
        });
    }
}
