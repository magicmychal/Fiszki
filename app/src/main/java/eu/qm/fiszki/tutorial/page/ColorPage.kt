package eu.qm.fiszki.tutorial.page

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cleveroad.slidingtutorial.TutorialOptions
import com.cleveroad.slidingtutorial.TutorialPageProvider
import com.cleveroad.slidingtutorial.TutorialSupportFragment
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.NavHostActivity

class ColorPage : TutorialSupportFragment() {

    override fun provideTutorialOptions(): TutorialOptions {
        return newTutorialOptionsBuilder(requireContext())
            .setPagesCount(4)
            .setPagesColors(
                intArrayOf(
                    ContextCompat.getColor(requireContext(), R.color.yellow),
                    ContextCompat.getColor(requireContext(), R.color.pressed_color),
                    ContextCompat.getColor(requireContext(), R.color.pistachio),
                    ContextCompat.getColor(requireContext(), R.color.patin)
                )
            )
            .setUseInfiniteScroll(true)
            .setUseAutoRemoveTutorialFragment(false)
            .setOnSkipClickListener {
                startActivity(Intent(requireActivity(), NavHostActivity::class.java))
                requireActivity().finish()
            }
            .setTutorialPageProvider(TutorialPageProvider<Fragment> { position ->
                when (position % 4) {
                    0 -> FirstPage()
                    1 -> SecondPage()
                    2 -> ThirdPage()
                    3 -> FourPage()
                    else -> throw IllegalArgumentException("Unknown position: $position")
                }
            })
            .build()
    }
}
