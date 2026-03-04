package eu.qm.fiszki.tutorial.page;

import com.cleveroad.slidingtutorial.PageSupportFragment;
import com.cleveroad.slidingtutorial.Direction;
import com.cleveroad.slidingtutorial.TransformItem;

import eu.qm.fiszki.R;

/**
 * Created by bgood on 2016-04-15.
 */
public class FourPage extends PageSupportFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.tutorial_page_four;
    }

    @Override
    protected TransformItem[] getTransformItems() {
        return new TransformItem[]{
                TransformItem.create(R.id.TutFourImage, Direction.LEFT_TO_RIGHT, 20),
        };
    }
}
