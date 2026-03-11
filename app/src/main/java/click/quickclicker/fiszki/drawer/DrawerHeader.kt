package click.quickclicker.fiszki.drawer

import android.app.Activity
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import click.quickclicker.fiszki.R

class DrawerHeader(private val activity: Activity) {

    fun build(): AccountHeaderView = AccountHeaderView(activity).apply {
        headerBackground = ImageHolder(R.drawable.header_background)
    }
}
