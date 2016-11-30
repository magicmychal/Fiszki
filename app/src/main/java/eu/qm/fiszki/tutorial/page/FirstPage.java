package eu.qm.fiszki.tutorial.page;

/**
 * Created by bgood on 2016-04-14.
 */
import com.cleveroad.slidingtutorial.PageFragment;
import com.cleveroad.slidingtutorial.TransformItem;

import eu.qm.fiszki.R;

public class FirstPage extends PageFragment {
    @Override
    protected int getLayoutResId() {
        return R.layout.tutorial_page_first;
    }

    @Override
    protected TransformItem[] provideTransformItems() {
        return new TransformItem[]{
                new TransformItem(R.id.TutFirstImage, true, 20),
        };
    }

}