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
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.SpinnerDeviceTypeAdapter;

public class AddDeviceFragment extends Fragment {
    public static final String RESULT_NEW_DEV_NAME = "RESULT_NEW_DEV_NAME";
    public static final String RESULT_NEW_DEV_CHANNEL = "RESULT_NEW_DEV_CHANNEL";
    public static final String RESULT_NEW_DEV_TYPE = "RESULT_NEW_DEV_TYPE";

    private Spinner deviceTypeSpinner;
    private Button btn_next;
    private EditText editTextNewDevName;
    private EditText editTextNewDevChannel;

    private String newDevName;
    private int newDevChannel;
    private DeviceType newDevType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_device, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_addDevTitle));

        deviceTypeSpinner = getView().findViewById(R.id.deviceType_spinner);
        btn_next = getView().findViewById(R.id.button_devNextAdd);
        editTextNewDevName = getView().findViewById(R.id.editTextDevNameAdd);
        editTextNewDevChannel = getView().findViewById(R.id.editTextDevChannelAdd);

        Context context = this.getContext();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        ArrayAdapter<DeviceType> adapter = new SpinnerDeviceTypeAdapter(this.getContext(), R.layout.spinneritem_adddevice_devicetype_layout, DeviceType.values());

        deviceTypeSpinner.setAdapter(adapter);
    }

    private void next(){
        newDevName = editTextNewDevName.getText().toString().trim();
        String newDevChannelAsString = editTextNewDevChannel.getText().toString();

        if (newDevName.isEmpty()){
            Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevName, Toast.LENGTH_LONG);
            return;
        }
        if (newDevChannelAsString.isEmpty()){
            Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevChannel, Toast.LENGTH_LONG);
            return;
        }
        newDevChannel = Integer.parseInt(newDevChannelAsString);
        newDevType = (DeviceType) deviceTypeSpinner.getSelectedItem();

        ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddDeviceSelectRoomFragment()).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(FragmentsEnum.ADD_DEVICES_FRAGMENT);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RESULT_NEW_DEV_NAME, newDevName);
        outState.putInt(RESULT_NEW_DEV_CHANNEL, newDevChannel);
        outState.putInt(RESULT_NEW_DEV_TYPE, newDevType.ordinal());
    }
}