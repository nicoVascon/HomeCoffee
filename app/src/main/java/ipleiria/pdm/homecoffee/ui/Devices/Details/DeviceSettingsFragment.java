package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class DeviceSettingsFragment extends Fragment {
    private Device selectedDevice;

    private TextView txt_devName;
    private TextView txt_devChannel;
    private TextView txt_devTypeName;
    private TextView txt_devTypeNumber;
    private ImageView imageView_devType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_settings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        int devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
        selectedDevice = HouseManager.getInstance().getDevice(devPosition);

        txt_devName = getView().findViewById(R.id.textView_DeviceName_SettingsFragment);
        txt_devChannel = getView().findViewById(R.id.textView_DeviceChannel_SettingsFragment);

        View deviceTypeLayout = getView().findViewById(R.id.DeviceTypeLayout);
        txt_devTypeName = deviceTypeLayout.findViewById(R.id.textViewDeviceName_spinner);
        txt_devTypeNumber = deviceTypeLayout.findViewById(R.id.textViewNumDevType_spinner);
        imageView_devType = deviceTypeLayout.findViewById(R.id.imageViewDeviceTypePhoto_spinner);

        txt_devName.setText(selectedDevice.getName());
        txt_devChannel.setText(String.valueOf(selectedDevice.getChannel()));

        txt_devTypeName.setText(selectedDevice.getType().toString());
        txt_devTypeNumber.setText("");
        switch (selectedDevice.getType()){
            case HUMIDITY:
                imageView_devType.setImageResource(R.drawable.humiditysensor);
                break;
            case TEMPERATURE:
                imageView_devType.setImageResource(R.drawable.temperaturesensor);
                break;
            case LIGHT:
                imageView_devType.setImageResource(R.drawable.lightsensor);
                break;
            case ACCELERATION:
                imageView_devType.setImageResource(R.drawable.accelerationsensor);
                break;
            case PRESSURE:
                imageView_devType.setImageResource(R.drawable.preassuresensor);
                break;
        }
    }
}