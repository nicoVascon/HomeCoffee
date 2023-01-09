package ipleiria.pdm.homecoffee.mqtt;

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
import ipleiria.pdm.homecoffee.model.Device;
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

    public void start_mqtt() {
        try {
            INSTANCE = new PahoDemo();
            INSTANCE.initDemo("v3/teste-rs2022@ttn/devices/eui-70b3d54990a17f82/up");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nException: " + e.getMessage());
        }
    }

    public synchronized void submitMessage() {
        if (!HouseManager.getString_send_ttn().isEmpty()) {
            INSTANCE.doDemo(HouseManager.getString_send_ttn());
            HouseManager.getString_send_ttn().clear();
        }

    }


    public void doDemo(HashMap<Integer, String> payload_to_send) {

        if (payload_to_send.isEmpty())
            return;

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
        byte[] jsonPayload = jsonString.getBytes();
        message.setPayload(jsonPayload);
        try {
            client.publish("v3/teste-rs2022@ttn/devices/eui-70b3d54990a17f82/down/push", message);
        } catch (MqttException e) {
            System.out.println(e);
        }
    }

    public void initDemo(String topic) throws MqttException {
        try {
            String username = "teste-rs2022@ttn";
            String password = "NNSXS.AINW4RAXBJWKUI2U756QHW2PY3VA3OJ3URDXBZA.RMNVJ7R5POJVUXEDSYQXRVGU5JMJAG22K6H57KJKJHR6KIXCRJFQ";
            String serverurl = "tcp://eu1.cloud.thethings.network:1883";
            String clientId = MqttClient.generateClientId();

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setAutomaticReconnect(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            client = new MqttClient(serverurl, clientId, null);
            client.connect(options);

            client.setCallback(this);
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
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
        HouseManager houseManager = HouseManager.getInstance();
        String text = message.toString();
        System.out.println(text);
        // Extract the string after "Published to:" and before ":{"
        int startIndex = text.indexOf("received_at") + "received_at".length();
        int endIndex = text.indexOf("uplink_message", startIndex);
        String extractedString1 = text.substring(startIndex, endIndex);


        // Extract the string after "decoded_payload" and before ","
        startIndex = text.indexOf("decoded_payload") + "decoded_payload".length();
        endIndex = text.indexOf("rx_metadata", startIndex);
        String extractedString2 = text.substring(startIndex, endIndex);
        text = "Data: "+extractedString1+"\n   Dados: "+extractedString2+"\n\n";
        System.out.println(text);
        try {
            HouseManager.getInstance().getMsgs_received().append(text+"\n");
        } catch (Exception e) {
            System.out.println("Crashou: " + e.getMessage());
        }

        try {
            String text_div = extractedString2.substring(3, extractedString2.length() - 3);
            System.out.println(text_div);
            String[] text_split = text_div.split(",");
            //System.out.println(text_split);
            String[] text_div2;
            String[] text_chan;
            String[] text_val;
            for (int i = 0; i < text_split.length; i++) {
                System.out.println(text_split[i]);
                text_div2=text_split[i].split(":");

                int last_index=text_div2[0].lastIndexOf("_");
                int chan = Integer.parseInt(text_div2[0].substring(last_index+1, text_div2[0].length() - 1));
                System.out.println(chan);
                System.out.println(text_div2[1]);
                Device device = houseManager.searchSensorChannel(chan);
                if(device instanceof Sensor){
                    ((Sensor)device).setValue(Double.parseDouble(text_div2[1]));
                }
            }

        }catch (Exception e){
            System.out.println("Morri muito: "+e.getMessage());
        }
        //{"analog_in_1":13,"...
//        startIndex = text.indexOf("decoded_payload") + "decoded_payload".length();
//        endIndex = text.indexOf("rx_metadata", startIndex);
//        String extractedString2 = text.substring(startIndex, endIndex);
//
//        startIndex = text.indexOf("decoded_payload") + "decoded_payload".length();
//        endIndex = text.indexOf("rx_metadata", startIndex);
//        String extractedString2 = text.substring(startIndex, endIndex);




//        Toast.makeText(MainActivity.getCurrentFragment().getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


}