package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.ui.Devices.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.AddDeviceSelectRoomFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class DeviceSettingsFragment extends Fragment {
    public static boolean editingDevice;

    private Device selectedDevice;

    private TextView txt_devName;
    private TextView txt_devChannel;
    private TextView txt_devTypeName;
    private ImageView imageView_devType;

    private TextView txt_devRoomName;
    private ImageView imageView_devRoom;

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
        Button btn_editDev = getView().findViewById(R.id.btn_editDev);
        Button btn_deleteDev = getView().findViewById(R.id.btn_deleteDev);
        Context context = this.getContext();
        btn_editDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editingDevice = true;
                ((MainActivity) context).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new AddDeviceFragment()).commit();
            }
        });
        btn_deleteDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.txt_AlertDialog_deleteTitle))
                        .setMessage(getResources().getString(R.string.txt_AlertDialog_deleteDev))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HouseManager.getInstance().removeDevice(devPosition);
                                MainActivity.clearFragmentsVisitedList();
                                DeviceDetailsFragment.addAsVisitedFragment = false;
                                HouseManager.getBundle().remove(DevicesFragment.RESULT_DEV_POSITION);
                                ((MainActivity) context).getSupportFragmentManager().beginTransaction().
                                        replace(R.id.fragment_container, new DevicesFragment()).commit();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(getResources().getString(R.string.txt_no), null)
                        .setIcon(R.drawable.delete_icon)
                        .show();
            }
        });

        View deviceTypeLayout = getView().findViewById(R.id.DeviceTypeLayout);
        txt_devTypeName = deviceTypeLayout.findViewById(R.id.textViewDeviceName_spinner);
        imageView_devType = deviceTypeLayout.findViewById(R.id.imageViewDeviceTypePhoto_spinner);

        View deviceRoomLayout = getView().findViewById(R.id.DeviceRoomLayout);
        txt_devRoomName = deviceRoomLayout.findViewById(R.id.textViewItemName);
        imageView_devRoom = deviceRoomLayout.findViewById(R.id.imageViewItemPhoto);

        initDevTypeLayout();
        initRoomLayout();
    }

    public void initDevTypeLayout(){
        txt_devName.setText(selectedDevice.getName());
        txt_devChannel.setText(String.valueOf(selectedDevice.getChannel()));

        txt_devTypeName.setText(selectedDevice.getType().toString());
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

    public void initRoomLayout(){
        Room devRoom = selectedDevice.getRoom();
        txt_devRoomName.setText(devRoom.getNome());
        switch (devRoom.getType()) {
            case BEDROOM:
                imageView_devRoom.setImageResource(R.drawable.bedroom_alternative);
                break;
            case KITCHEN:
                imageView_devRoom.setImageResource(R.drawable.kitchen);
                break;
            case LIVING_ROOM:
                imageView_devRoom.setImageResource(R.drawable.living_room);
                break;
            case OFFICE:
                imageView_devRoom.setImageResource(R.drawable.office);
                break;
            case BATHROOM:
                imageView_devRoom.setImageResource(R.drawable.bathroom);
                break;
        }
    }
}