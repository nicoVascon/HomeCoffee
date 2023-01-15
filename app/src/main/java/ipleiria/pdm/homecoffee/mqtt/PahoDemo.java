package ipleiria.pdm.homecoffee.mqtt;

import android.app.Activity;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Sensor;

public class PahoDemo implements MqttCallback, Serializable {
    public static PahoDemo INSTANCE;

    private MqttClient client;


    public PahoDemo() {}

    //Código adicionado para garantir que há só uma instância da classe HouseManager
    public static synchronized PahoDemo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PahoDemo();
        }
        return INSTANCE;
    }

    public void start_mqtt(Activity activity) {
        try {
            INSTANCE = new PahoDemo();
            INSTANCE.initDemo(activity,"messagesFromCroatia/data");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nException: " + e.getMessage());
        }
    }

    public synchronized void submitMessage() {
        INSTANCE.doDemo(HouseManager.getString_send_ttn());
    }


    public void doDemo(HashMap<Integer, String> payload_to_send) {
        if (payload_to_send == null || payload_to_send.isEmpty()){
            return;
        }
        System.out.println("Oiiiiiiiiiii Entrei para emviar");
        MqttMessage message = new MqttMessage();
        StringBuilder to_send = new StringBuilder("");

        // for each loop
        for (Integer channel : payload_to_send.keySet()) {
            to_send.append(payload_to_send.get(channel) + "/");
        }

        payload_to_send.clear();

        byte[] payload1Data = new byte[0];
        try {
            payload1Data = to_send.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String payload = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            payload = Base64.getEncoder().encodeToString(payload1Data);
        }

        String jsonString = "{\"end_device_ids\":{\"device_id\":\"dev1\",\"application_ids\":{\"application_id\":\"app1\"}},\"downlinks\":[{\"f_port\":15,\"frm_payload\":\"" + payload + "\",\"decoded_payload\":{\"temperature\":1.0,\"luminosity\":0.64},\"priority\":\"NORMAL\",\"confirmed\":true}]}";
        byte[] jsonPayload = to_send.toString().getBytes();
        message.setPayload(jsonPayload);
        try {
            client.publish("messagesFromCroatia/commands", message);
        } catch (MqttException e) {
            System.out.println(e);
        }
    }

    public void initDemo(Activity activity, String topic) throws MqttException {
        try {

            String serverurl = "tcp://broker.hivemq.com:1883";
            String clientId = MqttClient.generateClientId();

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(1);


            client = new MqttClient(serverurl, clientId, null);
            client.connect(options);

            client.setCallback(this);
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toastMessage_UnreachableServer),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public byte[] encodeTtnMessage(byte[] token, byte[] payload) {
        // Allocate a buffer with enough space for the header and the payload
        ByteBuffer buffer = ByteBuffer.allocate(4 + token.length + payload.length);

        // Encode the header
        buffer.put((byte) 3);  // Version
        buffer.put((byte) token.length);  // Token length
        buffer.putShort((short) payload.length);  // Payload length

        // Append the token and payload
        buffer.put(token);
        buffer.put(payload);

        // Return the encoded data
        return buffer.array();
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Conexao morreu por:" + cause.getMessage());

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (!HouseManager.isModificable()){
            return;
        }

        try{
            System.out.println("Oiiii");
            String textReceived = message.toString();
            System.out.println(textReceived);

            String[] textReceivedSplit = textReceived.split("/");

            for (String deviceMsg : textReceivedSplit){
                String[] deviceMsgValues = deviceMsg.split(":");
                if (deviceMsgValues.length == 2){
                    int channel = Integer.parseInt(deviceMsgValues[0]);
                    double value = Double.parseDouble(deviceMsgValues[1]);
                    Sensor sensor = HouseManager.getInstance().searchSensorByChannel(channel);
                    if(sensor != null){
                        sensor.setValue(value);
                    }

                    System.out.println("\nSensor Value Update: Channel: " + channel + " Value: " + value);
                }
            }
        }catch (Exception e){
            System.out.println("Exception messageArrived: " + e.getMessage());
        }


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


}