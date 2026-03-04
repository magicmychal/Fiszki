package eu.qm.fiszki.drawer.drawerItem

import android.app.Activity
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.withIcon
import com.mikepenz.materialdrawer.model.interfaces.withName
import eu.qm.fiszki.R

class Exit(activity: Activity) : PrimaryDrawerItem() {

    init {
        withName(R.string.drawer_exit_name)
        withIcon(R.drawable.ic_exit_to_app_black_24px)
        withOnDrawerItemClickListener { _, _, _ ->
            activity.finish()
            false
        }
    }
}
