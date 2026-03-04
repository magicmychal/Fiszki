package eu.qm.fiszki.drawer.drawerItem

import android.app.Activity
import android.content.Intent
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.withIcon
import com.mikepenz.materialdrawer.model.interfaces.withName
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.NavHostActivity

class Settings(activity: Activity) : PrimaryDrawerItem() {

    init {
        withName(R.string.drawer_settings_name)
        withIcon(R.drawable.ic_settings_black_24px)
        withOnDrawerItemClickListener { _, _, _ ->
            activity.startActivity(Intent(activity, NavHostActivity::class.java).apply {
                putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_settings)
            })
            false
        }
    }
}
