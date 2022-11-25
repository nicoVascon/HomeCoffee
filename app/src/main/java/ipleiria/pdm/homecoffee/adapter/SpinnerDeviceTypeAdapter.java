package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ipleiria.pdm.homecoffee.DeviceType;
import ipleiria.pdm.homecoffee.R;

public class SpinnerDeviceTypeAdapter extends ArrayAdapter<DeviceType> {


    public SpinnerDeviceTypeAdapter(Context context, int resource, DeviceType[] deviceTypes){
        super(context, resource, deviceTypes);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout = inflater.inflate(R.layout.spinneritem_adddevice_devicetype_layout, parent, false);
        TextView txtDevName_devType = layout.findViewById(R.id.textViewDeviceName_spinner);
        TextView txtNumDevices_devType = layout.findViewById(R.id.textViewNumDevType_spinner);
        ImageView imgPhoto_devType = layout.findViewById(R.id.imageViewDeviceTypePhoto_spinner);

        DeviceType deviceType = DeviceType.values()[position];
        txtDevName_devType.setText(deviceType.toString());
        switch (deviceType){
            case HUMIDITY:
                imgPhoto_devType.setImageResource(R.drawable.humiditysensor);
                break;
            case TEMPERATURE:
                imgPhoto_devType.setImageResource(R.drawable.temperaturesensor);
                break;
            case LIGHT:
                imgPhoto_devType.setImageResource(R.drawable.lightsensor);
                break;
            case ACCELERATION:
                imgPhoto_devType.setImageResource(R.drawable.accelerationsensor);
                break;
            case PRESSURE:
                imgPhoto_devType.setImageResource(R.drawable.preassuresensor);
                break;
        }
        txtNumDevices_devType.setText("10");
        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
