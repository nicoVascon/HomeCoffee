package ipleiria.pdm.homecoffee.ui.Devices;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;

public class DeviceDetailsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_devDetails));
    }
}