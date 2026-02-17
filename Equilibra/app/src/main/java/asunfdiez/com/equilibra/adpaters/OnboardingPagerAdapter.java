package asunfdiez.com.equilibra.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import asunfdiez.com.equilibra.activities.OnboardingFragment;

public class OnboardingPagerAdapter extends FragmentStateAdapter {
    private static final int SCREEN_COUNT = 5; // Emotional + 4 information screens
    private String[] screenTypes = {
            "emotional",
            "calm",
            "worried",
            "confused",
            "final"
    };

    public OnboardingPagerAdapter(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OnboardingFragment.newInstance(screenTypes[position]);
    }

    @Override
    public int getItemCount() {
        return SCREEN_COUNT;
    }
}
