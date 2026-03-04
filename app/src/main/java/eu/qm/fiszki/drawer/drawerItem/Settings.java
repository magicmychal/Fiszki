package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;
import android.content.Intent;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.SettingsActivity;

public class Settings extends PrimaryDrawerItem {

    public Settings(final Activity activity) {
        NameableKt.withName(this, R.string.drawer_settings_name);
        IconableKt.withIcon(this, R.drawable.ic_settings_black_24px);
        this.withOnDrawerItemClickListener((view, drawerItem, position) -> {
            activity.startActivity(new Intent(activity, SettingsActivity.class));
            return false;
        });
    }
}
