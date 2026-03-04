package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import eu.qm.fiszki.R;

/**
 * Created by tm on 08.07.16.
 */
public class Exit extends PrimaryDrawerItem {

    public Exit(final Activity activity) {
        NameableKt.withName(this, R.string.drawer_exit_name);
        IconableKt.withIcon(this, R.drawable.ic_exit_to_app_black_24px);
        this.withOnDrawerItemClickListener((view, drawerItem, position) -> {
            activity.finish();
            return false;
        });
    }
}
