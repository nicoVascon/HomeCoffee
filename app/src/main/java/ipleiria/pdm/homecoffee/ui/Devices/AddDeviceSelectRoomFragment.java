package ipleiria.pdm.homecoffee.ui.Devices;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;

public class AddDeviceSelectRoomFragment extends Fragment implements SaveData {
    public static final String RESULT_NEW_DEV_ROOM = "RESULT_NEW_DEV_ROOM";

    private RecyclerView mRecyclerView;
    private RecycleRoomsAdapter mAdapter;
    private Button addButton;

    private String newDevName;
    private int newDevChannel;
    private DeviceType newDevType;
    private Room newDevRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recoverData();
        return inflater.inflate(R.layout.fragment_add_device_select_room, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_addDevTitle));

        addButton = getView().findViewById(R.id.button_devAdd_SelectRoom);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceSettingsFragment.editingDevice){
                    edit();
                }else {
                    add();
                }
            }
        });
        if(DeviceSettingsFragment.editingDevice){
            addButton.setText(getResources().getString(R.string.btn_save));
            MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_editDevTitle));
        }else {
            addButton.setText(getResources().getString(R.string.btn_addDevice));
        }
        mRecyclerView = getView().findViewById(R.id.RecyclerViewAddDevSelectRoom);
        mAdapter = new RecycleRoomsAdapter(this.getActivity()){
            @Override
            public void onBindViewHolder(@NonNull RoomsHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                Bundle mbundle = HouseManager.getBundle();
                if(mbundle.containsKey(RESULT_NEW_DEV_ROOM)){
                    int lastSelectedRoomPosition = mbundle.getInt(RESULT_NEW_DEV_ROOM);
                    if (lastSelectedRoomPosition == position){
                        onItemClick(holder.itemView, position);
                    }
                }
            }

            @Override
            public void onItemClick(View v, int position){
                super.onItemClick(v, position);
                newDevRoom = HouseManager.getInstance().getRoom(position);
                Bundle mbundle = HouseManager.getBundle();
                mbundle.putInt(RESULT_NEW_DEV_ROOM, position);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));
    }

    private void edit(){
        if (newDevRoom != null){
            int devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
            Device selectedDevice = HouseManager.getInstance().getDevice(devPosition);
            selectedDevice.setName(newDevName);
            selectedDevice.setChannel(newDevChannel);
            selectedDevice.setType(newDevType);
            selectedDevice.setRoom(newDevRoom);
            ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new DeviceDetailsFragment(DeviceDetailsFragment.SETTINGS_TAB_INDEX)).commit();
            return;
        }
        Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevRoom, Toast.LENGTH_LONG).show();
    }

    private void add(){
        if (newDevRoom != null){
            HouseManager.getInstance().addDevice(new Device(newDevChannel, newDevName, newDevType, newDevRoom));
            ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new DevicesFragment()).commit();
            HouseManager.setBundle(null);
            MainActivity.clearFragmentsVisitedList();
            return;
        }
        Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevRoom, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(FragmentsEnum.ADD_DEVICES_SELECT_ROOM_FRAGMENT);
    }

    @Override
    public void saveData() {

    }

    @Override
    public void recoverData() {
        try {
            Bundle bundle = HouseManager.getBundle();
            newDevName = bundle.getString(AddDeviceFragment.RESULT_NEW_DEV_NAME);
            newDevChannel = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_CHANNEL);
            newDevType = DeviceType.values()[bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_TYPE)];
        }catch (NullPointerException e){
            System.out.println("Erro ao recuperar os dados do novo dispositivo.");
            System.out.println(e.getMessage());
            ((MainActivity) this.getActivity()).setInitialFragment();
        }
    }
}