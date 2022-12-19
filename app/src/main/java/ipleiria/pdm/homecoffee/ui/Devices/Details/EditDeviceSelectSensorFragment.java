package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesMiniAdapter;
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceSelectRoomFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class EditDeviceSelectSensorFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecycleDevicesMiniAdapter<Sensor> dAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_sensor, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_selectSensorTitle));

        HouseManager houseManager = HouseManager.getInstance();

        mRecyclerView = getView().findViewById(R.id.recyclerViewSelectSensor);
        Bundle bundle = HouseManager.getBundle();
        int actuatorPosition = bundle.getInt(DevicesFragment.RESULT_DEV_POSITION);
        Actuator actuator = (Actuator) houseManager.getDevice(actuatorPosition);
        dAdapter = new RecycleDevicesMiniAdapter<Sensor>(this.getActivity(),
                houseManager.searchSensors(actuator.getType())){
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                Sensor sensorToAssociate = getItem(position);
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.txt_AlertDialog_AssociateSensorTitle))
                        .setMessage(getResources().getString(R.string.txt_AlertDialog_associateSensor) +
                                "\n\n\"" + sensorToAssociate.getName() + '"')
                        .setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = HouseManager.getBundle();
                                if (bundle == null){
                                    bundle = new Bundle();
                                    HouseManager.setBundle(bundle);
                                }
                                int actuatorPosition = bundle.getInt(DevicesFragment.RESULT_DEV_POSITION);
                                Actuator actuator = (Actuator) houseManager.getDevice(actuatorPosition);
                                Sensor currentAssociatedSensor = actuator.setAssociatedSensor(sensorToAssociate);
                                if(currentAssociatedSensor == sensorToAssociate){
                                    Toast.makeText(getContext(), getResources().getString(R.string.toastMessage_AssociateSensorSuccess),
                                            Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getContext(), getResources().getString(R.string.toastMessage_AssociateSensorFail),
                                            Toast.LENGTH_LONG).show();
                                }
                                ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().
                                        replace(R.id.fragment_container, new DeviceDetailsFragment(DeviceDetailsFragment.CONTROL_TAB_INDEX)).commit();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.txt_no), null)
                        .setIcon(R.drawable.link_icon)
                        .show();

            }
        };
        mRecyclerView.setAdapter(dAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.EDIT_DEVICES_SELECT_ROOM_FRAGMENT);
    }
}