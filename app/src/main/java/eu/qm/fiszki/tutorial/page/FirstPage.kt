package eu.qm.fiszki.tutorial.page

import com.cleveroad.slidingtutorial.Direction
import com.cleveroad.slidingtutorial.PageSupportFragment
import com.cleveroad.slidingtutorial.TransformItem
import eu.qm.fiszki.R

class FirstPage : PageSupportFragment() {

    override fun getLayoutResId(): Int = R.layout.tutorial_page_first

    override fun getTransformItems(): Array<TransformItem> = arrayOf(
        TransformItem.create(R.id.TutFirstImage, Direction.LEFT_TO_RIGHT, 20f)
    )
}
