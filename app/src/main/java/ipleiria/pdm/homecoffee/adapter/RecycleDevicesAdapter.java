package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;

import ipleiria.pdm.homecoffee.Device;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.Room;

public class RecycleDevicesAdapter extends RecyclerView.Adapter<RecycleDevicesAdapter.DevicesHolder> {
    private HouseManager gestorContactos;
    private Context context;
    private LayoutInflater mInflater;

    public RecycleDevicesAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.gestorContactos = HouseManager.getInstance();
        this.context=context;
    }

    public RecycleDevicesAdapter.DevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_layout,parent, false);
        return new RecycleDevicesAdapter.DevicesHolder(mItemView, this);
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
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleDevicesAdapter.DevicesHolder holder, int position) {
        Device devCurrent = gestorContactos.getDevice(position);
        holder.txtDevName.setText(devCurrent.getName());
        holder.txtConnectionState.setText(devCurrent.isConnectionState() ? R.string.txt_connectionStateConnected : R.string.txt_connectionStateDisconected);
        holder.switchDev.setChecked(devCurrent.isConnectionState());
        holder.txtNumDev.setText(Integer.toString(devCurrent.getNumero()));
        if (devCurrent.getPathPhoto().trim().isEmpty()) {
            holder.imgPhoto.setImageResource(R.drawable.ic_no_photo);
        } else {
            try {
                File f=new File(context.getFilesDir() + "/" +
                        devCurrent.getPathPhoto());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                holder.imgPhoto.setImageBitmap(b);
            } catch (Exception e) {
                holder.imgPhoto.setImageResource(R.drawable.ic_no_photo);
            }
        }
        holder.itemView.setLongClickable(true);
        holder.itemView.setClickable(true);
    }
    @Override
    public int getItemCount() {
        return gestorContactos.getDevices().size();
    }
}
