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
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Sensor;

/**
 * Classe PahoDemo que implementa a interface MqttCallback e Serializable, para exemplificar o uso da biblioteca Paho
 * para se comunicar com o protocolo MQTT
 */
public class PahoDemo implements MqttCallback, Serializable {
    public static PahoDemo INSTANCE;

    /**
     * Atributo que armazena uma conexão com um cliente MQTT
     */
    private MqttClient client;

    /**
     * Construtor padrão da classe
     */
    public PahoDemo() {}

    //Código adicionado para garantir que há só uma instância da classe HouseManager
    /**
     * Método que garante que existe apenas uma instância da classe PahoDemo.
     * @return Instância atual da classe PahoDemo
     */
    public static synchronized PahoDemo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PahoDemo();
        }
        return INSTANCE;
    }
    /**
     * Inicia o cliente MQTT.
     * @param  activity a actividade onde é executado
     * @throws MqttException caso ocorra algum erro ao iniciar o cliente
     */
    public void start_mqtt(Activity activity) {
        try {
            INSTANCE = new PahoDemo();
//            INSTANCE.initDemo(activity,"messagesFromCroatia/data");
            INSTANCE.initDemo(activity,"HOMECOFFEE/" + HouseManager.getInstance().getUser().getId().substring(0, 5) +
                    "/data");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nException: " + e.getMessage());
        }
    }

    /**
     * Envia uma mensagem para o tópico especificado.
     * A mensagem é obtida a partir do método getString_send_ttn() da classe HouseManager.
     */
    public synchronized void submitMessage() {
        INSTANCE.doDemo(HouseManager.getString_send_ttn());
    }

    /**
     * Método que envia a mensagem para o tópico mqtt especificado.
     * @param payload_to_send HashMap com os payloads a serem enviados.
     */
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
//            client.publish("messagesFromCroatia/commands", message);
            client.publish("HOMECOFFEE/" + HouseManager.getInstance().getUser().getId().substring(0, 5) +
                    "/commands", message);
        } catch (MqttException e) {
            System.out.println(e);
        }
    }
    /**
     * Método responsável por inicializar a conexão com o MQTT Broker
     * utilizando o protocolo MQTT.
     *
     * @param topic Tópico ao qual deseja se inscrever para receber mensagens
     * @throws MqttException Exceção lançada em caso de erro ao se conectar ao MQTT Broker
     */
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

    /**
     * Codifica uma mensagem para o formato TTN (The Things Network)
     *
     * @param token o token de autenticação
     * @param payload o conteúdo da mensagem
     * @return o array de bytes codificado
     */
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
    /**
     * Este método é chamado quando a conexão é perdida.
     * Ele imprime a mensagem de erro da causa da perda de conexão.
     *
     * @param cause a causa da perda de conexão
     */
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Conexao morreu por:" + cause.getMessage());

    }
    /**
     * Este método é chamado quando uma mensagem é recebida.
     * Ele processa a mensagem recebida, separando-a em diferentes valores e atualizando os sensores correspondentes.
     *
     * @param topic Tópico que a mensagem foi publicada
     * @param message Conteúdo da mensagem publicada
     * @throws Exception se ocorrer algum erro durante o processamento da mensagem
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (!HouseManager.isModificable()){
            return;
        }

        try{
            System.out.println("Oiiii");
            String textReceived = message.toString();
            System.out.println(textReceived);
            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String data = dateFormatter.format(currentDate.getTimeInMillis());
            HouseManager.msgs_received.append("\nDate: " + data + "\n");
            HouseManager.msgs_received.append("Data: " + textReceived + "\n");

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
    /**
     * Este método é chamado quando a entrega de uma mensagem publicada é completa.
     * Ele é geralmente usado para limpar qualquer estado relacionado à entrega ou para notificar o usuário que a entrega foi concluída.
     *
     * @param token token relacionado a publicação
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


}