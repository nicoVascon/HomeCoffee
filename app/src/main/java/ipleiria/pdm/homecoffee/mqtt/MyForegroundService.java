package ipleiria.pdm.homecoffee.mqtt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryFragment;

public class MyForegroundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "homecoffee";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform your task here

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// Create the channel
        CharSequence name = "HomeCoffee";
        String description = "HomeCoffee channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(description);
        }

// Register the channel with the system
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }


        runForever();

        // Show a notification to the user
        startForeground(NOTIFICATION_ID, createNotification());

        // Return START_STICKY to indicate that the service should continue running
        // until it is explicitly stopped, even if the app is closed.
        return START_STICKY;
    }
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Getting house data")
                .setContentText("Running in the background...")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }

    private void runForever() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PahoDemo.getInstance().start_mqtt(MainActivity.getCurrentFragment().getActivity());
                while(true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (HouseManager.isModificable()){
                        PahoDemo.getInstance().submitMessage();
                    }

//                    (MainActivity.getCurrentFragment().getActivity()).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            if(GalleryFragment.textLogs!=null){
//                                StringBuilder msgs_received = HouseManager.getInstance().getMsgs_received();
//                                GalleryFragment.textLogs.setText(msgs_received.toString());
//                            }
//                            DeviceActivityFragment.updateValues();
//
//                        }
//                    });
                }
            }
        }) ;
        thread.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }
}
