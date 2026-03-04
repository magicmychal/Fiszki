package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;
import android.content.Intent;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IconableKt;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import eu.qm.fiszki.R;
import eu.qm.fiszki.tutorial.TutorialActivity;

/**
 * Created by Siusiacz on 09.07.2016.
 */
public class Tutorial extends PrimaryDrawerItem {

    public Tutorial(final Activity activity) {
        NameableKt.withName(this, R.string.drawer_tutorial_name);
        IconableKt.withIcon(this, R.drawable.help_circle);
        this.withOnDrawerItemClickListener((view, drawerItem, position) -> {
            Intent goTutorial = new Intent(activity, TutorialActivity.class);
            activity.startActivity(goTutorial);
            activity.finish();
            return false;
        });
    }
}
