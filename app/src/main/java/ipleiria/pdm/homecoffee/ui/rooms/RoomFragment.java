package ipleiria.pdm.homecoffee.ui.rooms;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
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

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RoomFragment extends Fragment {
    public static final String RESULT_ROOM_POSITION = "RESULT_ROOM_POSITION";
    public static final String RESULT_DEV_POSITION = "RESULT_DEV_POSITION";

    private HouseManager houseManager;
    private RecyclerView mRecyclerView;
    private RecycleDevicesAdapter dAdapter;
    private TextView txtDevConState;
    private Switch allDevSwitch;
    private Button addDeviceButton;
    private static boolean devicesEnable;
    private int room_position;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.ROOM_FRAGMENT);

        // "MainActivity.clearFragmentsVisitedList();" This method is unnecessary due FragmentsEnum.ROOM_FRAGMENT is not
        // defined in the Backpressed method, so the application will return to the HomeFragment (default).
        // This implementation is better than clear the FragmentVisited List due keep a user route part.
        // This Fragment must not be part of the user route.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        room_position = getArguments().getInt(RESULT_ROOM_POSITION);
        System.out.println(room_position);
    }

    @Override
    public void onStart() {
        Context context = this.getContext();
        super.onStart();
        houseManager = HouseManager.getInstance();
        ArrayList<Device> devices = houseManager.getRoom(room_position).getDevices();

        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_devicesTitle));

        mRecyclerView = getView().findViewById(R.id.recyclerViewDevices);
        dAdapter = new RecycleDevicesAdapter(this.getActivity() ,this,devices){
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
//                Bundle bundle = HouseManager.getBundle();
//                if (bundle == null){
//                    bundle = new Bundle();
//                    HouseManager.setBundle(bundle);
//                }
//                bundle.putInt(RESULT_DEV_POSITION, position);
                DeviceDetailsFragment.setSelectedDevice(devices.get(position));

                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new DeviceDetailsFragment()).commit();
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
                DevicesFragment.setDevicesEnable(isChecked);
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
                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new AddDeviceFragment()).commit();
            }
        });

        updateDevicesConnectionState();

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
        RoomFragment.devicesEnable = devicesEnable;
    }

}