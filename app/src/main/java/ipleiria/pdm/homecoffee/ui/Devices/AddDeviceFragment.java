package ipleiria.pdm.homecoffee.ui.Devices;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.SpinnerDeviceTypeAdapter;
import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;

public class AddDeviceFragment extends Fragment implements SaveData {
    public static final String RESULT_NEW_DEV_NAME = "RESULT_NEW_DEV_NAME";
    public static final String RESULT_NEW_DEV_CHANNEL = "RESULT_NEW_DEV_CHANNEL";
    public static final String RESULT_NEW_DEV_TYPE = "RESULT_NEW_DEV_TYPE";

    private Spinner deviceTypeSpinner;
    private Button btn_next;
    private EditText editTextNewDevName;
    private EditText editTextNewDevChannel;

    private String newDevName;
    private int newDevChannel;
    private DeviceType newDevType = DeviceType.values()[0];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recoverData();

        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        if(DeviceSettingsFragment.editingDevice){
            MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_editDevTitle));
        }else {
            MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_addDevTitle));
        }

        deviceTypeSpinner = getView().findViewById(R.id.deviceType_spinner);
        btn_next = getView().findViewById(R.id.button_devNextAdd);
        editTextNewDevName = getView().findViewById(R.id.editTextDevNameAdd);
        editTextNewDevChannel = getView().findViewById(R.id.editTextDevChannelAdd);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        ArrayAdapter<DeviceType> adapter = new SpinnerDeviceTypeAdapter(this.getContext(), R.layout.spinneritem_adddevice_devicetype_layout, DeviceType.values());
        deviceTypeSpinner.setAdapter(adapter);

        editTextNewDevName.setText(newDevName);
        editTextNewDevChannel.setText(String.valueOf(newDevChannel));
        deviceTypeSpinner.setSelection(newDevType.ordinal());
    }

    private void next(){
        newDevName = editTextNewDevName.getText().toString().trim();
        String newDevChannelAsString = editTextNewDevChannel.getText().toString();

        if (newDevName.isEmpty()){
            Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevName, Toast.LENGTH_LONG).show();
            return;
        }
        if (newDevChannelAsString.isEmpty()){
            Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevChannel, Toast.LENGTH_LONG).show();
            return;
        }
        saveData();

        ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new AddDeviceSelectRoomFragment()).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MainActivity.addFragmentViseted(FragmentsEnum.ADD_DEVICES_FRAGMENT);
    }

    @Override
    public void saveData() {
        newDevName = editTextNewDevName.getText().toString().trim();
        String newDevChannelAsString = editTextNewDevChannel.getText().toString();
        newDevChannel = newDevChannelAsString.isEmpty() ? 0: Integer.parseInt(newDevChannelAsString);
        newDevType = (DeviceType) deviceTypeSpinner.getSelectedItem();
        Bundle bundle = HouseManager.getBundle();
        if(bundle == null){
            bundle = new Bundle();
            HouseManager.setBundle(bundle);
        }
        bundle.putString(RESULT_NEW_DEV_NAME, newDevName);
        bundle.putInt(RESULT_NEW_DEV_CHANNEL, newDevChannel);
        bundle.putInt(RESULT_NEW_DEV_TYPE, newDevType.ordinal());
    }

    @Override
    public void recoverData() {
        Bundle bundle = HouseManager.getBundle();
        if (bundle != null){
            if(bundle.containsKey(AddDeviceFragment.RESULT_NEW_DEV_NAME)){
                newDevName = bundle.getString(AddDeviceFragment.RESULT_NEW_DEV_NAME);
                newDevChannel = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_CHANNEL);
                int devTypePosition = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_TYPE);
                newDevType = DeviceType.values()[devTypePosition];
            }else if (DeviceSettingsFragment.editingDevice) {
                int selectedDevPosition = bundle.getInt(DevicesFragment.RESULT_DEV_POSITION);
                Device deviceToEdit = HouseManager.getInstance().getDevice(selectedDevPosition);
                newDevName = deviceToEdit.getName();
                newDevChannel = deviceToEdit.getChannel();
                newDevType = deviceToEdit.getType();
                int selectedDevRoomPosition = HouseManager.getInstance().getRoomIndex(deviceToEdit.getRoom());
                bundle.putInt(AddDeviceSelectRoomFragment.RESULT_NEW_DEV_ROOM, selectedDevRoomPosition);
            }
        }
    }
}