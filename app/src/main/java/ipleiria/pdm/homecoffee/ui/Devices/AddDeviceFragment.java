package ipleiria.pdm.homecoffee.ui.Devices;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import ipleiria.pdm.homecoffee.Device;
import ipleiria.pdm.homecoffee.DeviceType;
import ipleiria.pdm.homecoffee.R;

public class AddDeviceFragment extends Fragment {
    private Spinner deviceTypeSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        deviceTypeSpinner = getView().findViewById(R.id.deviceType_spinner);

        ArrayList<String> arrayList = new ArrayList<>();
        for (DeviceType deviceType : DeviceType.values()){
            arrayList.add(deviceType.toString());
        }

        ArrayAdapter<DeviceType> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, DeviceType.values());
        deviceTypeSpinner.setAdapter(adapter);
    }
}