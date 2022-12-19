package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Sensor;

public class RecycleDevicesMiniAdapter<D extends Device> extends RecyclerView.Adapter<RecycleDevicesMiniAdapter.DevicesHolder> {
    private HouseManager gestorContactos;
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<D> itemsList;

    public RecycleDevicesMiniAdapter(Context context, ArrayList<D> itemsList){
        mInflater = LayoutInflater.from(context);
        this.gestorContactos = HouseManager.getInstance();
        this.context=context;
        this.itemsList = itemsList;
    }

    public RecycleDevicesMiniAdapter.DevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_device_mini_layout,parent, false);
        return new DevicesHolder(mItemView, this);
    }

    public class DevicesHolder extends RecyclerView.ViewHolder {
        public final TextView txtDevName;
        public final ImageView imgPhoto;
        public final CardView cardView_dev;
        public final RecycleDevicesMiniAdapter dAdapter;

        public DevicesHolder(@NonNull View itemView,  RecycleDevicesMiniAdapter adapter) {
            super(itemView);

            txtDevName = itemView.findViewById(R.id.textViewDeviceName_mini);
            imgPhoto= itemView.findViewById(R.id.imageViewDevicePhoto_mini);
            cardView_dev = itemView.findViewById(R.id.cardView_dev_mini);
            this.dAdapter = adapter;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleDevicesMiniAdapter.DevicesHolder holder, int position) {
        D devCurrent = itemsList.get(position);
        holder.txtDevName.setText(devCurrent.getName());
        holder.cardView_dev.setCardBackgroundColor(
                (devCurrent instanceof Sensor) ?
                        context.getResources().getColor(R.color.devIconBackground) :
                        context.getResources().getColor(R.color.devTypeSpinnerIconBackground));
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
        holder.itemView.setClickable(true);
        int itemPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, itemPosition);
            }
        });
    }

    public D getItem(int position){
        return itemsList.get(position);
    }

    public void onItemClick(View view, int position){
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public Context getContext() {
        return context;
    }
}
