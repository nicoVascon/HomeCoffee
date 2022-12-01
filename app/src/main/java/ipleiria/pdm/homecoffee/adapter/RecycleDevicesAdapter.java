package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class RecycleDevicesAdapter extends RecyclerView.Adapter<RecycleDevicesAdapter.DevicesHolder> {
    private HouseManager gestorContactos;
    private Context context;
    private LayoutInflater mInflater;
    DevicesFragment devicesFragment;

    public RecycleDevicesAdapter(Context context, DevicesFragment devicesFragment){
        mInflater = LayoutInflater.from(context);
        this.gestorContactos = HouseManager.getInstance();
        this.context=context;
        this.devicesFragment = devicesFragment;
    }

    public RecycleDevicesAdapter.DevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_device_layout,parent, false);
        return new DevicesHolder(mItemView, this);
    }

    public class DevicesHolder extends RecyclerView.ViewHolder {
        public final TextView txtConnectionState;
        public final TextView txtDevName;
        public final TextView txtNumDev;
        public final Switch switchDev;
        public final ImageView imgPhoto;
        final RecycleDevicesAdapter dAdapter;

        public DevicesHolder(@NonNull View itemView,  RecycleDevicesAdapter adapter) {
            super(itemView);

            txtConnectionState = itemView.findViewById(R.id.textViewConnectionState);
            txtDevName = itemView.findViewById(R.id.textViewDeviceName);
            txtNumDev = itemView.findViewById(R.id.textViewNumDev);
            switchDev = itemView.findViewById(R.id.switch_device);
            imgPhoto= itemView.findViewById(R.id.imageViewDevicePhoto);
            this.dAdapter = adapter;

            switchDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Device itemDevice = getDevice();

                    itemDevice.setConnectionState(isChecked);

                    if (DevicesFragment.isDevicesEnable()){
                        devicesFragment.updateDevicesConnectionState();
//                        dAdapter.notifyDataSetChanged();
                    }
                    txtConnectionState.setText(isChecked ? R.string.txt_connectionStateConnected : R.string.txt_connectionStateDisconected);
                    buttonView.setText(isChecked ? R.string.btn_OnDevices : R.string.btn_OffDevices);
                }
            });
        }

        public Device getDevice(){
            return gestorContactos.getDevice(this.getLayoutPosition());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleDevicesAdapter.DevicesHolder holder, int position) {
        Device devCurrent = gestorContactos.getDevices().get(position);
        holder.txtDevName.setText(devCurrent.getName());
        if (!DevicesFragment.isDevicesEnable()){
            devCurrent.setConnectionState(false);
            holder.switchDev.setEnabled(false);
        } else{
            holder.switchDev.setEnabled(true);
        }
        holder.txtConnectionState.setText(devCurrent.isConnectionState() ? R.string.txt_connectionStateConnected : R.string.txt_connectionStateDisconected);
        holder.switchDev.setChecked(devCurrent.isConnectionState());
        holder.switchDev.setText(devCurrent.isConnectionState() ? R.string.btn_OnDevices : R.string.btn_OffDevices);
        holder.txtNumDev.setText(String.format("%.2f", devCurrent.getValue()) + " %");
        switch (devCurrent.getType()){
            case HUMIDITY:
                holder.imgPhoto.setImageResource(R.drawable.humiditysensor);
                break;
            case TEMPERATURE:
                holder.imgPhoto.setImageResource(R.drawable.temperaturesensor);
                break;
            case LIGHT:
                holder.imgPhoto.setImageResource(R.drawable.lightsensor);
                break;
            case ACCELERATION:
                holder.imgPhoto.setImageResource(R.drawable.accelerationsensor);
                break;
            case PRESSURE:
                holder.imgPhoto.setImageResource(R.drawable.preassuresensor);
                break;
        }
        holder.itemView.setLongClickable(true);
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, position);
            }
        });
    }

    public void onItemClick(View view, int position){
    }

    @Override
    public int getItemCount() {
        return gestorContactos.getDevices().size();
    }

    public Context getContext() {
        return context;
    }
}
