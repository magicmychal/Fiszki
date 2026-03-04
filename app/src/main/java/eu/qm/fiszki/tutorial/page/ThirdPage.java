package eu.qm.fiszki.tutorial.page;

/**
 * Created by bgood on 2016-04-14.
 */
import com.cleveroad.slidingtutorial.PageSupportFragment;
import com.cleveroad.slidingtutorial.Direction;
import com.cleveroad.slidingtutorial.TransformItem;

import eu.qm.fiszki.R;

public class ThirdPage extends PageSupportFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.tutorial_page_third;
    }

    @Override
    protected TransformItem[] getTransformItems() {
        return new TransformItem[]{
                TransformItem.create(R.id.TutThirdImage, Direction.LEFT_TO_RIGHT, 20),
        };
    }
}
