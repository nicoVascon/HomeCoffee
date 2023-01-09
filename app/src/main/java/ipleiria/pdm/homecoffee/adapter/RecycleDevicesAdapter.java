package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;

public class RecycleDevicesAdapter extends RecyclerView.Adapter<RecycleDevicesAdapter.DevicesHolder> {
    private HouseManager houseManager;
    private Context context;
    private LayoutInflater mInflater;
    private DevicesFragment devicesFragment;
    private RoomFragment roomFragment;
    private ArrayList<Device> devices;

    public RecycleDevicesAdapter(Context context, DevicesFragment devicesFragment, ArrayList<Device> devices){
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context=context;
        this.devicesFragment = devicesFragment;
        this.devices=devices;
    }

    public RecycleDevicesAdapter(Context context,RoomFragment roomFragment, ArrayList<Device> devices){
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context=context;
        this.devices=devices;
        this.roomFragment=roomFragment;
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
        public final CardView cardView_dev;
        public final RecycleDevicesAdapter dAdapter;

        public DevicesHolder(@NonNull View itemView,  RecycleDevicesAdapter adapter) {
            super(itemView);

            txtConnectionState = itemView.findViewById(R.id.textViewConnectionState);
            txtDevName = itemView.findViewById(R.id.textViewDeviceName);
            txtNumDev = itemView.findViewById(R.id.textViewDevValue);
            switchDev = itemView.findViewById(R.id.switch_device);
            imgPhoto= itemView.findViewById(R.id.imageViewDevicePhoto);
            cardView_dev = itemView.findViewById(R.id.cardView_dev);
            this.dAdapter = adapter;

            switchDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Device itemDevice = getDevice();

                    itemDevice.setConnectionState(isChecked);

                    if (DevicesFragment.isDevicesEnable()){
                        if(devicesFragment!=null) {
                            devicesFragment.updateDevicesConnectionState();
                        }
                        else {
                            roomFragment.updateDevicesConnectionState();
//                        dAdapter.notifyDataSetChanged();
                        }
                    }
                    txtConnectionState.setText(isChecked ? R.string.txt_connectionStateConnected : R.string.txt_connectionStateDisconected);
                    buttonView.setText(isChecked ? R.string.btn_OnDevices : R.string.btn_OffDevices);
                }
            });
        }

        public Device getDevice(){
            return houseManager.getDevice(this.getLayoutPosition());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleDevicesAdapter.DevicesHolder holder, int position) {
        if (position == 0){
            String gatewayName = houseManager.getGatewayBLEServerName();
            holder.txtDevName.setText(gatewayName != null && !gatewayName.trim().isEmpty()? gatewayName :
                    context.getResources().getString(R.string.txt_GatewayNotDefined));
            holder.txtConnectionState.setText(getContext().getResources().getString(R.string.txt_DeviceEUICode) +
                    (gatewayName != null && !gatewayName.trim().isEmpty()?
                            houseManager.getGatewayBLEServerDevEuiCode() : ""));
            holder.txtNumDev.setText("");
            holder.switchDev.setVisibility(View.GONE);
            holder.imgPhoto.setImageResource(R.drawable.gateway_icon);
            holder.cardView_dev.setCardBackgroundColor(
                            context.getResources().getColor(android.R.color.holo_red_light));
            holder.itemView.setLongClickable(true);
            holder.itemView.setClickable(true);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(context.getResources().getString(R.string.txt_AlertDialog_DisassociateGatewayTitle))
                            .setMessage(context.getResources().getString(R.string.txt_AlertDialog_DisassociateGateway) +
                                    "\n\n\"" + HouseManager.getInstance().getGatewayBLEServerName() + "\"")
                            .setPositiveButton(context.getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    HouseManager.getInstance().setGatewayBLEServerName(null);
                                    HouseManager.getInstance().setGatewayBLEServerDevEuiCode(null);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(context.getResources().getString(R.string.txt_no), null)
                            .setIcon(R.drawable.link_icon)
                            .show();
                }
            });
            return;
        }


        Device devCurrent = devices.get(position - 1);
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
        holder.cardView_dev.setCardBackgroundColor(
                (devCurrent instanceof Sensor) ?
                        context.getResources().getColor(R.color.devIconBackground) :
                        context.getResources().getColor(R.color.devTypeSpinnerIconBackground));
        switch (devCurrent.getType()){
            case DIGITAL:
                holder.imgPhoto.setImageResource(R.drawable.digital_icon);
                break;
            case ANALOG:
                holder.imgPhoto.setImageResource(R.drawable.analog_icon);
                break;
            case PRESENCE:
                holder.imgPhoto.setImageResource(R.drawable.presence_icon);
                break;
            case HUMIDITY:
                holder.imgPhoto.setImageResource(R.drawable.humiditysensor);
                break;
            case TEMPERATURE:
                holder.imgPhoto.setImageResource(R.drawable.temperaturesensor);
                break;
            case LUMINOSITY:
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
        int itemPosition = position - 1;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, itemPosition);
            }
        });
    }

    public void onItemClick(View view, int position){
    }

    @Override
    public int getItemCount() {
        return devices.size() +1;
    }

    public Context getContext() {
        return context;
    }
}
