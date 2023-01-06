package ipleiria.pdm.homecoffee.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.thethingsnetwork.data.mqtt.Client;

import java.net.URISyntaxException;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;

public class GalleryFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_galleryTitle));

        String region = "eu";
        String appId = "teste-2023";
        String accessKey = "NNSXS.JHCBMXLI22CXIMYMVKMFBNCVL5Z64B62SCPVKLY.HCM6TRL2BXT5BD6KWNL5GA367ORXKQ3V6KYOHWDJEYLZKTLKWGLQ";

//        PahoDemo pahoDemo = new PahoDemo();
//        pahoDemo.doDemo();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.GALLERY_FRAGMENT);
    }

    public class PahoDemo implements MqttCallback {

        MqttClient client;

        public PahoDemo() {
        }

        public void main() {
            new PahoDemo().doDemo();
        }

        public void doDemo() {
            try {
                String username   = "teste-2023@ttn";
                String password   = "NNSXS.JHCBMXLI22CXIMYMVKMFBNCVL5Z64B62SCPVKLY.HCM6TRL2BXT5BD6KWNL5GA367ORXKQ3V6KYOHWDJEYLZKTLKWGLQ";
//                String serverurl  = "tcp://broker.hivemq.com:1883";
                String serverurl  = "tcp://eu1.cloud.thethings.network:1883";
                String clientId   = MqttClient.generateClientId();

                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setAutomaticReconnect(true);
                options.setUserName(username);
                options.setPassword(password.toCharArray());

                client = new MqttClient(serverurl, clientId, null);
                client.connect(options);

                client.setCallback(this);
//                client.subscribe("messagesFromCroatia/#");
                client.subscribe("v3/teste-2023@ttn/devices/eui-70b3d54995b0444b/service/data");

                MqttMessage message = new MqttMessage();
                message.setPayload("{\"downlinks\":[{\"f_port\": 15,\"frm_payload\":\"vu8=\",\"priority\": \"NORMAL\"}]}".getBytes());
                client.publish("v3/teste-2023@ttn/devices/eui-70b3d54995b0444b/down/push", message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            // TODO Auto-generated method stub

        }

        @Override
        public void messageArrived(String topic, MqttMessage message)
                throws Exception {
            System.out.println(message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // TODO Auto-generated method stub

        }

    }

}
