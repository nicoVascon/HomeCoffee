package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class RecycleNotificationsAdapter extends RecyclerView.Adapter<RecycleNotificationsAdapter.NotificationsHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private Device selectedDevice;

    public RecycleNotificationsAdapter(Context context, Device selectedDevice){
        mInflater = LayoutInflater.from(context);
        this.context=context;

        this.selectedDevice = selectedDevice;
    }

    public RecycleNotificationsAdapter.NotificationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_notification_layout,parent, false);
        return new NotificationsHolder(mItemView, this);
    }

    public class NotificationsHolder extends RecyclerView.ViewHolder {
        public final TextView txtNotificationDate;
        public final TextView txtNotificationDescription;
        public final ImageView imgPhoto;
        public final Button deleteButton;
        final RecycleNotificationsAdapter dAdapter;

        public NotificationsHolder(@NonNull View itemView, RecycleNotificationsAdapter adapter) {
            super(itemView);

            txtNotificationDate = itemView.findViewById(R.id.textViewNotificationDate);
            txtNotificationDescription = itemView.findViewById(R.id.textViewNotificationDescription);
            imgPhoto= itemView.findViewById(R.id.imageViewNotificationPhoto);
            deleteButton = itemView.findViewById(R.id.btn_deleteNotification);
            this.dAdapter = adapter;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleNotificationsAdapter.NotificationsHolder holder, int position) {
        Notification currentNotification = this.selectedDevice.getNotification(position);

        holder.txtNotificationDate.setText(currentNotification.getDateFormatted());
        holder.txtNotificationDescription.setText(currentNotification.getDescription());
        holder.imgPhoto.setImageResource(R.drawable.notification_icon);
        holder.imgPhoto.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        int notificationPosition = position;
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(notificationPosition);
                notifyDataSetChanged();
            }
        });

    }

    public void onDeleteClick(int position){
        selectedDevice.removeNotification(position);
    }

    @Override
    public int getItemCount() {
        return selectedDevice.getNumNotifications();
    }

    public Context getContext() {
        return context;
    }
}
