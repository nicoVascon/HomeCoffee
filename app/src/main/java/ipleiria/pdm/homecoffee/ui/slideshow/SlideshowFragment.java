package ipleiria.pdm.homecoffee.ui.slideshow;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.Tests.Tab1Fragment;
import ipleiria.pdm.homecoffee.Tests.Tab2Fragment;
import ipleiria.pdm.homecoffee.Tests.Tab3Fragment;
import ipleiria.pdm.homecoffee.adapter.TabAdapter;
import ipleiria.pdm.homecoffee.databinding.FragmentSlideshowBinding;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceControlFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class SlideshowFragment extends Fragment {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String[] tabsTitles = {"Control", "Activity", "Schedule", "Settings"};
    private int[] tabsSelectedIcon = {
            R.drawable.camera_tab_icon2,
            R.drawable.statistics_tab_icon,
            R.drawable.schedule_tab_icon,
            R.drawable.settings_cute_tab_icon
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slideshow, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_slideshowTitle));

        Bundle bundle = HouseManager.getBundle();
        if (bundle == null){
            bundle = new Bundle();
            HouseManager.setBundle(bundle);
        }
        bundle.putInt(DevicesFragment.RESULT_DEV_POSITION, 1);

        viewPager = (ViewPager2) getView().findViewById(R.id.viewPager);
        tabLayout = (TabLayout) getView().findViewById(R.id.tabLayout);
        adapter = new TabAdapter(this);
        //adapter.addFragment(new Tab1Fragment(), "Tab 1");
        adapter.addFragment(new DeviceControlFragment(), "Tab 1");
//        adapter.addFragment(new Tab2Fragment(), "Tab 2");
        adapter.addFragment(new DeviceActivityFragment(), "Tab 2");
        adapter.addFragment(new Tab3Fragment(), "Tab 3");
        adapter.addFragment(new Tab3Fragment(), "Tab 4");
        viewPager.setAdapter(adapter);

        tabLayout.setBackgroundColor(getResources().getColor(R.color.white));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_color));
        tabLayout.setTabTextColors(getResources().getColor(R.color.gray), getResources().getColor(R.color.app_color));
        tabLayout.setTabIconTint(null);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setIcon(tabsSelectedIcon[position]);
            tab.setText(tabsTitles[position]);
            if (position > 0){
                int tabIconColor = ContextCompat.getColor(this.getContext(), R.color.gray);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
        }).attach();

        Context context = this.getContext();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(null);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(context, R.color.gray);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Optional :)
        String contentDescription = "Tab Layout Fixe";
        tabLayout.setContentDescription(contentDescription);
        String contentDescriptionTab1 = "Tab 1 Muito Fixe";
        tabLayout.getTabAt(0).setContentDescription(contentDescriptionTab1);

        BadgeDrawable  badge = tabLayout.getTabAt(1).getOrCreateBadge();
        badge.setNumber(25);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.SLIDES_HOW_FRAGMENT);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}