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
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceControlFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class SlideshowFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slideshow, container, false);
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