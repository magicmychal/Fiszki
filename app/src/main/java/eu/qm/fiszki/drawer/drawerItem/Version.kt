package eu.qm.fiszki.drawer.drawerItem

import android.app.Activity
import android.content.pm.PackageManager
import com.mikepenz.materialdrawer.model.SectionDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.withName
import eu.qm.fiszki.R

class Version(activity: Activity) : SectionDrawerItem() {

    init {
        val version = try {
            activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }

        withName("${activity.resources.getString(R.string.drawer_version_ver)}$version")
    }
}
