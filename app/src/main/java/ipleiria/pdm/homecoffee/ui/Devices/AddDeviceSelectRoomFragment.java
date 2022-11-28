package ipleiria.pdm.homecoffee.ui.Devices;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;

public class AddDeviceSelectRoomFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecycleRoomsAdapter mAdapter;

    private String newDevName;
    private int newDevChannel;
    private DeviceType newDevType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            newDevName = savedInstanceState.getString(AddDeviceFragment.RESULT_NEW_DEV_NAME);
            newDevChannel = savedInstanceState.getInt(AddDeviceFragment.RESULT_NEW_DEV_CHANNEL);
            newDevType = DeviceType.values()[savedInstanceState.getInt(AddDeviceFragment.RESULT_NEW_DEV_TYPE)];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_device_select_room, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mRecyclerView = getView().findViewById(R.id.RecyclerViewAddDevSelectRoom);
        mAdapter = new RecycleRoomsAdapter(this.getActivity()){
            @Override
            public void onItemClick(int position){
                System.out.println("Oiiiiiiiiiii Número: " + position);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));
    }

    private void add(){
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(FragmentsEnum.ADD_DEVICES_SELECT_ROOM_FRAGMENT);
    }

}