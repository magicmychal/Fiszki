package eu.qm.fiszki.drawer;

import android.app.Activity;

import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.util.MaterialDrawerSliderViewExtensionsKt;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;

import eu.qm.fiszki.drawer.drawerItem.Exit;
import eu.qm.fiszki.drawer.drawerItem.Frequenc;
import eu.qm.fiszki.drawer.drawerItem.SelectCategory;
import eu.qm.fiszki.drawer.drawerItem.Settings;
import eu.qm.fiszki.drawer.drawerItem.SwitchNotyfication;
import eu.qm.fiszki.drawer.drawerItem.Version;

public class DrawerMain {

    private final Activity mActivity;

    public DrawerMain(Activity activity) {
        this.mActivity = activity;
    }

    public void setup(MaterialDrawerSliderView slider) {
        slider.setAccountHeader(new DrawerHeader(mActivity).build());
        MaterialDrawerSliderViewExtensionsKt.addItems(slider,
                new Frequenc(mActivity),
                new SelectCategory(mActivity),
                new DividerDrawerItem(),
                new Settings(mActivity),
                new Exit(mActivity),
                new Version(mActivity)
        );
        MaterialDrawerSliderViewExtensionsKt.addStickyDrawerItems(slider,
                new SwitchNotyfication(mActivity)
        );
    }
}
