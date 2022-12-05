package ipleiria.pdm.homecoffee.ui.Devices;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ipleiria.pdm.homecoffee.Tests.Tab3Fragment;
import ipleiria.pdm.homecoffee.adapter.TabAdapter;
import ipleiria.pdm.homecoffee.components.CircleSliderView;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceControlFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;

public class DeviceDetailsFragment extends Fragment implements SaveData {
    private Device selectedDevice;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recoverData();
        return inflater.inflate(R.layout.fragment_device_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_devDetails));

//        Bundle bundle = HouseManager.getBundle();
//        if (bundle == null){
//            bundle = new Bundle();
//            HouseManager.setBundle(bundle);
//        }
//        bundle.putInt(DevicesFragment.RESULT_DEV_POSITION, 1);

        viewPager = (ViewPager2) getView().findViewById(R.id.viewPager);
        tabLayout = (TabLayout) getView().findViewById(R.id.tabLayout);
        adapter = new TabAdapter(this);
        //adapter.addFragment(new Tab1Fragment(), "Tab 1");
        adapter.addFragment(new DeviceControlFragment(), "Tab 1");
//        adapter.addFragment(new Tab2Fragment(), "Tab 2");
        adapter.addFragment(new DeviceActivityFragment(), "Tab 2");
        adapter.addFragment(new Tab3Fragment(), "Tab 3");
//        adapter.addFragment(new Tab3Fragment(), "Tab 4");
        adapter.addFragment(new DeviceSettingsFragment(), "Tab 3");
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
        viewPager.setUserInputEnabled(false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(null);
                if (tab.getPosition() == 0){
                    viewPager.setUserInputEnabled(false);
                }else{
                    viewPager.setUserInputEnabled(true);
                }
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

        BadgeDrawable badge = tabLayout.getTabAt(1).getOrCreateBadge();
        badge.setNumber(25);

    }

    @Override
    public void saveData() {

    }

    @Override
    public void recoverData() {
        Bundle bundle = HouseManager.getBundle();
        int selectedDevPosition = bundle.getInt(DevicesFragment.RESULT_DEV_POSITION);
        this.selectedDevice = HouseManager.getInstance().getDevice(selectedDevPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.DEVICE_DETAILS_FRAGMENT);
    }
}