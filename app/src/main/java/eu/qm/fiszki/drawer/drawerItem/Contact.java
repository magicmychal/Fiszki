package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import eu.qm.fiszki.R;

/**
 * Created by Siusiacz on 09.07.2016.
 */
public class Contact extends PrimaryDrawerItem {

    public Contact(final Activity activity) {
        NameableKt.withName(this, R.string.drawer_contact_name);
        IconableKt.withIcon(this, R.drawable.send);
        this.withOnDrawerItemClickListener((view, drawerItem, position) -> false);
    }
}
