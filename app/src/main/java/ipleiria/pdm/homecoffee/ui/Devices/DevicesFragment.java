package ipleiria.pdm.homecoffee.ui.Devices;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.databinding.FragmentDevicesBinding;
import ipleiria.pdm.homecoffee.databinding.FragmentGalleryBinding;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryViewModel;

public class DevicesFragment extends Fragment {
    private HouseManager houseManager;
    private RecyclerView mRecyclerView;
    private RecycleDevicesAdapter dAdapter;
    private TextView txtDevConState;
    private Switch allDevSwitch;
    private Button addDeviceButton;
    private static boolean devicesEnable;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        houseManager = HouseManager.getInstance();

        mRecyclerView = getView().findViewById(R.id.recyclerViewDevices);
        dAdapter = new RecycleDevicesAdapter(this.getActivity(), this);
        mRecyclerView.setAdapter(dAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        txtDevConState = getView().findViewById(R.id.textViewDevicesConState);
        allDevSwitch = getView().findViewById(R.id.switchAllDevState);
        allDevSwitch.setChecked(devicesEnable);
        allDevSwitch.setText(devicesEnable ? R.string.btn_enable_dev : R.string.btn_disabled_dev);

        updateDevicesConnectionState();

        allDevSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                devicesEnable = isChecked;
                if (isChecked){
                    houseManager.recoverSavedDeviceConnectionState();
                }else{
                    houseManager.saveDeviceConnectionState();
                }

                dAdapter.notifyDataSetChanged();
                txtDevConState.setText(isChecked ? R.string.txt_DevicesConnected : R.string.txt_DevicesDisconnected);
                buttonView.setText(isChecked ? R.string.btn_enable_dev : R.string.btn_disabled_dev);
            }
        });

        addDeviceButton = getView().findViewById(R.id.btn_addDevice);
        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddDeviceFragment()).commit();
            }
        });

    }

    public void updateDevicesConnectionState(){
        int numberOfDevicesConnect = houseManager.numberOfDevicesConnect();
        txtDevConState.setText(numberOfDevicesConnect > 0 ?
                numberOfDevicesConnect + getResources().getString(R.string.txt_DevicesConnected)  : getResources().getString(R.string.txt_DevicesDisconnected));
    }

    public static boolean isDevicesEnable() {
        return devicesEnable;
    }

    public static void setDevicesEnable(boolean devicesEnable) {
        DevicesFragment.devicesEnable = devicesEnable;
    }
}
