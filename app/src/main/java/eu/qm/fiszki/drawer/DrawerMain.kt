package eu.qm.fiszki.drawer

import android.app.Activity
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.util.addItems
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import eu.qm.fiszki.drawer.drawerItem.Exit
import eu.qm.fiszki.drawer.drawerItem.SelectCategory
import eu.qm.fiszki.drawer.drawerItem.Settings
import eu.qm.fiszki.drawer.drawerItem.Version

class DrawerMain(private val activity: Activity) {

    fun setup(slider: MaterialDrawerSliderView) {
        slider.accountHeader = DrawerHeader(activity).build()
        slider.addItems(
            SelectCategory(activity),
            DividerDrawerItem(),
            Settings(activity),
            Exit(activity),
            Version(activity)
        )
    }
}
