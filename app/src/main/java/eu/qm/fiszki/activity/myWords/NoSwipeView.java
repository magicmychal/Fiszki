package eu.qm.fiszki.activity.myWords;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Siusiacz on 23.07.2016.
 */
public class NoSwipeView extends ViewPager {

    public NoSwipeView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
