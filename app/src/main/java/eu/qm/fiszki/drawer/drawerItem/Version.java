package eu.qm.fiszki.drawer.drawerItem;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.NameableKt;

import eu.qm.fiszki.R;

/**
 * Created by tm on 08.07.16.
 */
public class Version extends SectionDrawerItem {

    public Version(Activity activity) {

        PackageManager manager = activity.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info != null ? info.versionName : "";

        NameableKt.withName(this, activity.getResources().getString(R.string.drawer_version_ver) + version);
    }
}
