package ipleiria.pdm.homecoffee.ui.Devices.Add;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class AddDeviceFragment extends Fragment implements SaveData {
    public static final String RESULT_NEW_DEV_NAME = "RESULT_NEW_DEV_NAME";
    public static final String RESULT_NEW_DEV_CHANNEL = "RESULT_NEW_DEV_CHANNEL";
    public static final String RESULT_NEW_DEV_TYPE = "RESULT_NEW_DEV_TYPE";
    public static final String RESULT_NEW_DEV_MODE = "RESULT_NEW_DEV_MODE";

    private Spinner deviceModeSpinner;
    private Button btn_next;
    private EditText editTextNewDevName;
    private TextView txt_devTypeName;
    private ImageView imageView_devType;

    private String newDevName;
    private int newDevChannel;
    private DeviceType newDevType = DeviceType.values()[0];
    private int newDevMode = 0;

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

        deviceModeSpinner = getView().findViewById(R.id.deviceMode_spinner);
        btn_next = getView().findViewById(R.id.button_devNextAdd);
        editTextNewDevName = getView().findViewById(R.id.editTextDevNameAdd);

        View deviceTypeLayout = getView().findViewById(R.id.DeviceTypeLayout);
        txt_devTypeName = deviceTypeLayout.findViewById(R.id.textViewDeviceName_spinner);
        imageView_devType = deviceTypeLayout.findViewById(R.id.imageViewDeviceTypePhoto_spinner);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

//        SpinnerDeviceTypeAdapter adapter = new SpinnerDeviceTypeAdapter(this.getContext(),
//                R.layout.item_devicetype_layout, DeviceType.values());
//        deviceTypeSpinner.setAdapter(adapter);
        String[] deviceModeNames = {getContext().getResources().getString(R.string.devModeSpinner_Sensor),
                                    getContext().getResources().getString(R.string.devModeSpinner_Actuator)};
        ArrayAdapter<String> devModeArrayAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_1, deviceModeNames);
        deviceModeSpinner.setAdapter(devModeArrayAdapter);

        editTextNewDevName.setText(newDevName);
//        editTextNewDevChannel.setText(String.valueOf(newDevChannel));
//        deviceTypeSpinner.setSelection(newDevType.ordinal());
        deviceModeSpinner.setSelection(newDevMode);

        initDevTypeLayout();
    }

    private void next(){
        newDevName = editTextNewDevName.getText().toString().trim();
//        String newDevChannelAsString = editTextNewDevChannel.getText().toString();

        if (newDevName.isEmpty()){
            Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevName, Toast.LENGTH_LONG).show();
            return;
        }
//        if (newDevChannelAsString.isEmpty()){
//            Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevChannel, Toast.LENGTH_LONG).show();
//            return;
//        }
        saveData();

        if(deviceModeSpinner.getSelectedItemPosition() == 0) {
            ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new AddDeviceSelectRoomFragment()).commit();
        }else{
            ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new AddDeviceSelectSensorFragment()).commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MainActivity.addFragmentViseted(FragmentsEnum.ADD_DEVICES_FRAGMENT);
    }

    public void initDevTypeLayout(){

        switch (newDevType){
            case HUMIDITY:
                imageView_devType.setImageResource(R.drawable.humiditysensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Humidity));
                break;
            case TEMPERATURE:
                imageView_devType.setImageResource(R.drawable.temperaturesensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Temperature));
                break;
            case LUMINOSITY:
                imageView_devType.setImageResource(R.drawable.lightsensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Light));
                break;
            case ACCELERATION:
                imageView_devType.setImageResource(R.drawable.accelerationsensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Acceleration));
                break;
            case PRESSURE:
                imageView_devType.setImageResource(R.drawable.preassuresensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Pressure));
                break;
        }
    }

    @Override
    public void saveData() {
        newDevName = editTextNewDevName.getText().toString().trim();
//        String newDevChannelAsString = editTextNewDevChannel.getText().toString();
        String newDevChannelAsString = "";
        newDevChannel = newDevChannelAsString.isEmpty() ? 0: Integer.parseInt(newDevChannelAsString);
//        newDevType = (DeviceType) deviceTypeSpinner.getSelectedItem();
        newDevMode = deviceModeSpinner.getSelectedItemPosition();
        Bundle bundle = HouseManager.getBundle();
        if(bundle == null){
            bundle = new Bundle();
            HouseManager.setBundle(bundle);
        }
        bundle.putString(RESULT_NEW_DEV_NAME, newDevName);
        bundle.putInt(RESULT_NEW_DEV_CHANNEL, newDevChannel);
        bundle.putInt(RESULT_NEW_DEV_TYPE, newDevType.ordinal());
        bundle.putInt(RESULT_NEW_DEV_MODE, newDevMode);
    }

    @Override
    public void recoverData() {
        Bundle bundle = HouseManager.getBundle();
        if (bundle != null){
            int devTypePosition = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_TYPE);
            newDevType = DeviceType.values()[devTypePosition];
            newDevMode = bundle.getInt(RESULT_NEW_DEV_MODE);

            if(bundle.containsKey(AddDeviceFragment.RESULT_NEW_DEV_NAME)){
                newDevName = bundle.getString(AddDeviceFragment.RESULT_NEW_DEV_NAME);
                newDevChannel = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_CHANNEL);
                devTypePosition = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_TYPE);
                newDevType = DeviceType.values()[devTypePosition];
                newDevMode = bundle.getInt(RESULT_NEW_DEV_MODE);
            }else if (DeviceSettingsFragment.editingDevice) {
                int selectedDevPosition = bundle.getInt(DevicesFragment.RESULT_DEV_POSITION);
                Device deviceToEdit = HouseManager.getInstance().getDevice(selectedDevPosition);
                newDevName = deviceToEdit.getName();
                newDevChannel = deviceToEdit.getChannel();
                newDevType = deviceToEdit.getType();
                newDevMode = (deviceToEdit instanceof Sensor) ? 0 : 1;
                int selectedDevRoomPosition = HouseManager.getInstance().getRoomIndex(HouseManager.getInstance().searchRoomByDevice(deviceToEdit));
                bundle.putInt(AddDeviceSelectRoomFragment.RESULT_NEW_DEV_ROOM, selectedDevRoomPosition);
            }
        }
    }
}