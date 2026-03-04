package eu.qm.fiszki.drawer;

import android.app.Activity;

import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.widget.AccountHeaderView;

import eu.qm.fiszki.R;

/**
 * Created by Siusiacz on 06.07.2016.
 */
public class DrawerHeader {

    private final Activity mActivity;

    public DrawerHeader(Activity activity) {
        this.mActivity = activity;
    }

    public AccountHeaderView build() {
        AccountHeaderView headerView = new AccountHeaderView(mActivity);
        headerView.setHeaderBackground(new ImageHolder(R.drawable.header_background));
        return headerView;
    }
}
