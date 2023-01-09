package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.components.CircleSliderView;
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class DeviceControlFragment extends Fragment {
    //public static final String RESULT_DEV_POSITION = "RESULT_DEV_POSITION";

    private Device selectedDevice;

    private TextView textView_devName;
    private TextView textView_devMode;
    private TextView textView_actuatorSensorValue;
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

        TextView textView_CSDesiredValueLabel = getView().findViewById(R.id.textView_CSDesiredValueLabel);
        circleSlider_valueControl = getView().findViewById(R.id.circleSliderView_valueControler);
        TextView textView_CSDesiredValueLabel_btnOnOff = getView().findViewById(R.id.textView_CSDesiredValueLabel_btnOnOff);
        final Button customButton = getView().findViewById(R.id.btn_OnOff);

        if(selectedDevice.getType() == DeviceType.DIGITAL || selectedDevice.getType() == DeviceType.LUMINOSITY){
            textView_CSDesiredValueLabel.setVisibility(View.GONE);
            circleSlider_valueControl.setVisibility(View.GONE);

            textView_CSDesiredValueLabel_btnOnOff.setVisibility(View.VISIBLE);
            customButton.setVisibility(View.VISIBLE);
        }else{
            textView_CSDesiredValueLabel.setVisibility(View.VISIBLE);
            circleSlider_valueControl.setVisibility(View.VISIBLE);

            textView_CSDesiredValueLabel_btnOnOff.setVisibility(View.GONE);
            customButton.setVisibility(View.GONE);
        }

        customButton.setText(selectedDevice.getValue() == 1.0?
                getResources().getString(R.string.txt_On) :
                getResources().getString(R.string.txt_Off));
        customButton.setBackgroundTintList(selectedDevice.getValue() == 1.0?
                getResources().getColorStateList(R.color.ButtonOn) :
                getResources().getColorStateList(R.color.ButtonOff));
        customButton.setAlpha(selectedDevice.isConnectionState() ? 1.0f : 0.35f);
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
                ((Actuator) selectedDevice).setDesiredValue(selectedDevice.getValue() == 1.0? 0.0 : 1.0);
                customButton.setText(selectedDevice.getValue() == 1.0?
                        getResources().getString(R.string.txt_On) :
                        getResources().getString(R.string.txt_Off));
                customButton.setBackgroundTintList(selectedDevice.getValue() == 1.0?
                        getResources().getColorStateList(R.color.ButtonOn) :
                        getResources().getColorStateList(R.color.ButtonOff));
            }
        });



        textView_devName = getView().findViewById(R.id.textView_DeviceName_ControlFragment);
        textView_devMode = getView().findViewById(R.id.textView_DeviceMode_ControlFragment);


        textView_devName.setText(selectedDevice.getName());

        circleSlider_valueControl.setEnabled((selectedDevice instanceof Actuator) && selectedDevice.isConnectionState());
        customButton.setEnabled((selectedDevice instanceof Actuator) && selectedDevice.isConnectionState());
        circleSlider_valueControl.setAlpha(selectedDevice.isConnectionState() ||
                selectedDevice instanceof Sensor? 1.0f : 0.35f);
        circleSlider_valueControl.setmCurrentTime(selectedDevice.getValue()*3600/100);
        circleSlider_valueControl.setmCurrentRadian((float) ((selectedDevice.getValue()/100)*2*Math.PI));

        CardView cardView = getView().findViewById(R.id.cardView_ActuatorSensor);
        if(selectedDevice instanceof Sensor){
            cardView.setVisibility(View.GONE);
            textView_devMode.setText(getResources().getString(R.string.devModeSpinner_Sensor));
            textView_CSDesiredValueLabel.setText(getResources().getString(R.string.txt_valueControler_MeasuredValue));
        }else{
            textView_devMode.setText(getResources().getString(R.string.devModeSpinner_Actuator));
            textView_CSDesiredValueLabel.setText(getResources().getString(R.string.txt_valueControler_DesiredValue));

            cardView.setVisibility(View.VISIBLE);
            textView_actuatorSensorValue = getView().findViewById(R.id.textView_ActuadorSensorValue_ControlFragment);
            Button btn_associateSensor = getView().findViewById(R.id.btn_associateSensor);
            if(((Actuator) selectedDevice).getAssociatedSensor() != null){
                btn_associateSensor.setText(getResources().getString(R.string.btn_changeAssociateSensor));
                updateSensorValue();
            }else{
                btn_associateSensor.setText(getResources().getString(R.string.btn_associateSensor));
                textView_actuatorSensorValue.setText(getResources().getString(R.string.txt_NoAssociatedSensor));
            }
            //btn_associateSensor.setTextColor(getResources().getColor(R.color.app_color));
            btn_associateSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container, new EditDeviceSelectSensorFragment()).commit();
                }
            });
        }

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
                if (selectedDevice instanceof Actuator) {
                    ((Actuator) selectedDevice).setDesiredValue(percentValue);

                    //Temp code!!
                    if(((Actuator) selectedDevice).getAssociatedSensor() != null) {
                        ((Actuator) selectedDevice).getAssociatedSensor().setValue(percentValue);
                        textView_actuatorSensorValue.setText(String.format("%.2f", percentValue) + " " + unit);
                    }
                    //
                }

//                Integer dev_channel = selectedDevice.getChannel();
//                String dev_name = selectedDevice.getName();
//                String dev_value = String.format("%.2f",percentValue);
//
//                HouseManager.addString_send_ttn(dev_channel,"name:"+dev_name + ",dev_channel:" + dev_channel + ",dev_value:" + dev_value);

                return String.format("%.2f", percentValue) + " " + unit;
            }
        });

        devSwitch = getView().findViewById(R.id.switch_DevState_ControlFragment);
        devSwitch.setChecked(selectedDevice.isConnectionState());
        devSwitch.setText(selectedDevice.isConnectionState() ? R.string.btn_OnDevices : R.string.btn_OffDevices);
        devSwitch.setVisibility((selectedDevice instanceof Actuator)? View.VISIBLE : View.GONE);
        devSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedDevice.setConnectionState(isChecked);
                circleSlider_valueControl.setEnabled((selectedDevice instanceof Actuator) && isChecked);
                circleSlider_valueControl.setAlpha(isChecked ? 1.0f : 0.35f);
                customButton.setEnabled((selectedDevice instanceof Actuator) && isChecked);
                customButton.setAlpha(isChecked ? 1.0f : 0.35f);
                buttonView.setText(isChecked ? R.string.btn_OnDevices : R.string.btn_OffDevices);

                if (!isChecked){
                    selectedDevice.setValueSaved(((Actuator) selectedDevice).getValue());
                    ((Actuator) selectedDevice).setDesiredValue(0.0);
                }else{
                    ((Actuator) selectedDevice).setDesiredValue(selectedDevice.getValueSaved());
                }

                customButton.setText(selectedDevice.getValue() == 1.0?
                        getResources().getString(R.string.txt_On) :
                        getResources().getString(R.string.txt_Off));
                customButton.setBackgroundTintList(selectedDevice.getValue() == 1.0?
                        getResources().getColorStateList(R.color.ButtonOn) :
                        getResources().getColorStateList(R.color.ButtonOff));

            }
        });
    }

    public void updateSensorValue(){
        double measuredValue = ((Actuator) selectedDevice).getMeasuredValue();
        textView_actuatorSensorValue.setText(String.valueOf(measuredValue));
    }
}