package ipleiria.pdm.homecoffee.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class RecycleBLEDevicesAdapter extends RecyclerView.Adapter<RecycleBLEDevicesAdapter.BLEDevicesHolder> {
    private HouseManager houseManager;
    private Context context;
    private LayoutInflater mInflater;
    private List<BluetoothDevice> devices;

    private View lastSelectedItemView;

    public RecycleBLEDevicesAdapter(Context context, List<BluetoothDevice> devices) {
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context = context;
        this.devices = devices;
    }

    public RecycleBLEDevicesAdapter.BLEDevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_ble_device_layout, parent, false);
        return new RecycleBLEDevicesAdapter.BLEDevicesHolder(mItemView, this);
    }

    public class BLEDevicesHolder extends RecyclerView.ViewHolder {
        public final TextView textView_BLEDevName;
        public final ImageView imgPhoto;
        final RecycleBLEDevicesAdapter dAdapter;

        public BLEDevicesHolder(@NonNull View itemView, RecycleBLEDevicesAdapter adapter) {
            super(itemView);

            textView_BLEDevName = itemView.findViewById(R.id.textView_BLEDevName);
            imgPhoto = itemView.findViewById(R.id.imageViewBLEDevPhoto);
            this.dAdapter = adapter;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleBLEDevicesAdapter.BLEDevicesHolder holder, int position) {
        BluetoothDevice BLEDevice = this.devices.get(position);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        holder.textView_BLEDevName.setText(BLEDevice.getName() != null? BLEDevice.getName() :
                context.getResources().getString(R.string.txt_NoBLEDevName));
        holder.imgPhoto.setImageResource(R.drawable.ble_icon);
        //holder.imgPhoto.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        int itemPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectedItemView != null){
                    CardView lastCardView = lastSelectedItemView.findViewById(R.id.cardView_BLEDev);
                    lastCardView.setCardBackgroundColor(context.getResources().getColor(R.color.iconBackgoundRooms));
                }
                lastSelectedItemView = v;
                CardView cardView = v.findViewById(R.id.cardView_BLEDev);
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.devIconBackground));
                onItemClick(v, itemPosition);
            }
        });
    }

    public void onItemClick(View view, int position){
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public Context getContext() {
        return context;
    }
}
