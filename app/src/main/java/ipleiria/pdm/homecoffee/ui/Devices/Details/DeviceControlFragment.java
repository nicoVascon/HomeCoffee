package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.components.CircleSliderView;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class DeviceControlFragment extends Fragment {
    Device selectedDevice;

    TextView textView_devName;
    private Switch devSwitch;
    private CircleSliderView circleSlider_valueControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_control, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        int devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
        selectedDevice = HouseManager.getInstance().getDevice(devPosition);

        circleSlider_valueControl = getView().findViewById(R.id.circleSliderView_valueControler);
        circleSlider_valueControl.setEnabled(selectedDevice.isConnectionState());
        circleSlider_valueControl.setAlpha(selectedDevice.isConnectionState() ? 1.0f : 0.35f);
        circleSlider_valueControl.setmCurrentTime(selectedDevice.getValue()*3600/100);
        circleSlider_valueControl.setmCurrentRadian((float) ((selectedDevice.getValue()/100)*2*Math.PI));

        final String unit;
        if (selectedDevice.getType() == DeviceType.TEMPERATURE){
            unit = "ºC";
        }else {
            unit = "%";
        }
        circleSlider_valueControl.setOnTimeChangedListener(new CircleSliderView.OnTimeChangedListener() {
            @Override
            public void start(String starting) {
                System.out.println("Starting: " + starting);
            }

            @Override
            public void end(String ending) {
                System.out.println("Ending: " + ending);
            }

            @Override
            public String setText(double value) {
                double percentValue = value*100 / MAX_VALUE;
                selectedDevice.setValue(percentValue);
                return String.format("%.2f", percentValue) + " " + unit;
            }
        });

        devSwitch = getView().findViewById(R.id.switch_DevState_ControlFragment);
        devSwitch.setChecked(selectedDevice.isConnectionState());
        devSwitch.setText(selectedDevice.isConnectionState() ? R.string.btn_OnDevices : R.string.btn_OffDevices);
        devSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedDevice.setConnectionState(isChecked);
                circleSlider_valueControl.setEnabled(isChecked);
                circleSlider_valueControl.setAlpha(isChecked ? 1.0f : 0.35f);
                buttonView.setText(isChecked ? R.string.btn_OnDevices : R.string.btn_OffDevices);
            }
        });

        textView_devName = getView().findViewById(R.id.textView_DeviceName_ControlFragment);
        textView_devName.setText(selectedDevice.getName());


    }
}