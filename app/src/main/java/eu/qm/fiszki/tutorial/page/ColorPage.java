package eu.qm.fiszki.tutorial.page;

/**
 * Created by bgood on 2016-04-14.
 */

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cleveroad.slidingtutorial.TutorialOptions;
import com.cleveroad.slidingtutorial.TutorialPageProvider;
import com.cleveroad.slidingtutorial.TutorialSupportFragment;

import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.MainActivity;

public class ColorPage extends TutorialSupportFragment {

    @Override
    protected TutorialOptions provideTutorialOptions() {
        return TutorialSupportFragment.newTutorialOptionsBuilder(requireContext())
                .setPagesCount(4)
                .setPagesColors(new int[]{
                        ContextCompat.getColor(requireContext(), R.color.yellow),
                        ContextCompat.getColor(requireContext(), R.color.pressed_color),
                        ContextCompat.getColor(requireContext(), R.color.pistachio),
                        ContextCompat.getColor(requireContext(), R.color.patin)
                })
                .setUseInfiniteScroll(true)
                .setUseAutoRemoveTutorialFragment(false)
                .setOnSkipClickListener(v -> {
                    Intent myIntent = new Intent(requireActivity(), MainActivity.class);
                    startActivity(myIntent);
                    requireActivity().finish();
                })
                .setTutorialPageProvider(new TutorialPageProvider<Fragment>() {
                    @NonNull
                    @Override
                    public Fragment providePage(int position) {
                        switch (position % 4) {
                            case 0: return new FirstPage();
                            case 1: return new SecondPage();
                            case 2: return new ThirdPage();
                            case 3: return new FourPage();
                            default: throw new IllegalArgumentException("Unknown position: " + position);
                        }
                    }
                })
                .build();
    }
}
