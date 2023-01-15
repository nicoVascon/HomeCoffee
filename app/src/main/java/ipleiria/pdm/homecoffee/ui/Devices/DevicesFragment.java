package ipleiria.pdm.homecoffee.ui.Devices;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceSelectBLEDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Gateaway.GWConfig_BLEDeviceSelectionFragment;

public class DevicesFragment extends Fragment {
    public static final String RESULT_DEV_POSITION = "RESULT_DEV_POSITION";

    private HouseManager houseManager;
    private RecyclerView mRecyclerView;
    private static RecycleDevicesAdapter dAdapter;
    private TextView txtDevConState;
    private Switch allDevSwitch;
    private Button addDeviceButton;
    private static boolean devicesEnable = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.DEVICES_FRAGMENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        houseManager = HouseManager.getInstance();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_devicesTitle));

        mRecyclerView = getView().findViewById(R.id.recyclerViewDevices);
        dAdapter = new RecycleDevicesAdapter(this.getActivity(), this, HouseManager.getInstance().getDevices()){
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                Bundle bundle = HouseManager.getBundle();
                if (bundle == null){
                    bundle = new Bundle();
                    HouseManager.setBundle(bundle);
                }
                bundle.putInt(RESULT_DEV_POSITION, position);

                DeviceDetailsFragment deviceDetailsFragment = new DeviceDetailsFragment();
                deviceDetailsFragment.setSelectedDevice(HouseManager.getInstance().getDevice(position));
                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, deviceDetailsFragment).commit();
            }
        };
        mRecyclerView.setAdapter(dAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        txtDevConState = getView().findViewById(R.id.textViewDevicesConState);
        allDevSwitch = getView().findViewById(R.id.switchAllDevState);
        allDevSwitch.setChecked(devicesEnable);
        allDevSwitch.setText(devicesEnable ? R.string.btn_enable_dev : R.string.btn_disabled_dev);

        allDevSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                devicesEnable = isChecked;
                if (isChecked){
                    houseManager.recoverSavedActuatorValue();
                }else{
                    houseManager.saveActuatorValue();
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
                DeviceSettingsFragment.editingDevice = false;

                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.txt_AlertDialog_AddDeviceTitle))
                        .setMessage(getResources().getString(R.string.txt_AlertDialog_AddDevice))
                        .setPositiveButton(getResources().getString(R.string.txt_Automatic), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                                        replace(R.id.fragment_container, new AddDeviceSelectBLEDeviceFragment()).commit();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.txt_Manual), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                                        replace(R.id.fragment_container, new AddDeviceFragment()).commit();
                            }
                        })
                        .setIcon(R.drawable.add_icon)
                        .show();

            }
        });

        Button btnSetGateaway = getView().findViewById(R.id.btnSetGateaway);
        btnSetGateaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new GWConfig_BLEDeviceSelectionFragment()).commit();
            }
        });

        updateDevicesConnectionState();
    }

    public void updateDevicesConnectionState(){
        int numberOfDevicesConnect = houseManager.numberOfDevicesConnect(HouseManager.getInstance().getDevices());
        txtDevConState.setText(numberOfDevicesConnect > 0 ?
                numberOfDevicesConnect + getResources().getString(R.string.txt_DevicesConnected)  : getResources().getString(R.string.txt_DevicesDisconnected));
    }

    public static void updateDevicesValues(){
        if(dAdapter == null){
            return;
        }
        dAdapter.notifyDataSetChanged();
    }

    public static boolean isDevicesEnable() {
        return devicesEnable;
    }

    public static void setDevicesEnable(boolean devicesEnable) {
        DevicesFragment.devicesEnable = devicesEnable;
    }
}
