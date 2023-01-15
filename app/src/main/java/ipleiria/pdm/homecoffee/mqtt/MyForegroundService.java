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
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryFragment;

/**
 * Classe que representa o serviço de notificação do aplicativo. Ele implementa o método onStartCommand para inicializar o serviço, criação de notificações,
 *
 * e executa uma thread para coleta de dados da casa. Ele também implementa os métodos onBind e onDestroy para gerenciamento de ciclo de vida do serviço.
 */
public class MyForegroundService extends Service {

    /**
     * Método que é chamado quando o serviço é vinculado a outro componente. Ele retorna null nessa implementação.
     * @param intent Intent que originou a vinculação
     * @return null
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * ID da notificação
     */
    private static final int NOTIFICATION_ID = 1;
    /**
     * ID do canal
     */
    private static final String CHANNEL_ID = "homecoffee";

    /**
     * Método chamado quando o serviço é iniciado. Ele inicializa o serviço, criação de notificações,
     * e executa uma thread para coleta de dados da casa.
     * @param intent Intent que originou o início do serviço
     * @param flags Flags adicionais para o serviço
     * @param startId ID do início do serviço
     * @return Resultado da execução do serviço, como START_STICKY para indicar que o serviço deve continuar rodando até ser explicitamente parado
     */
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

    /**
     * Método que cria a notifiação que será mostrada pela aplicação quando estiver a adquirir dados da casa inteligente no background
     * @return a Notificação criada
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Getting house data")
                .setContentText("Running in the background...")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }

    /**
     * Método que inicia uma thread e executa a comunicação MQTT em segundo plano, enviando e recebendo mensagens
     * através do objeto PahoDemo. O loop é executado enquanto a variável HouseManager.isModificable() retornar true.
     */
    private void runForever() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PahoDemo.getInstance().start_mqtt(MainActivity.getCurrentFragment().getActivity());
                int i = 0;
                while(true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (HouseManager.isModificable()){
                        PahoDemo.getInstance().submitMessage();
                        i ++;
                        if(i==20){
                            i = 0;
                            for(Device device : HouseManager.getInstance().getDevices()){
                                device.update();
                            }
                        }
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

    /**
     * Método chamado quando o serviço é destruído. Utiliza o método stopForeground para parar a exibição da notificação
     * e o método stopSelf para parar o serviço.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }
}
