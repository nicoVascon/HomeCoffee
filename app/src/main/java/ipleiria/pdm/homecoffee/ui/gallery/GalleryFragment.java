package ipleiria.pdm.homecoffee.ui.gallery;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;

import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.mqtt.PahoDemo;

/**
 * Classe GalleryFragment é responsável por criar e gerenciar a interface de usuário para o fragmento de galeria.
 *
 * Ela também é responsável por gerenciar a comunicação entre o dispositivo Android e o gateway da casa (lopy).
 */
public class GalleryFragment extends Fragment {

    /**
     * Instância do gerenciador da casa.
     */
    private HouseManager houseManager;
    /**
     * Campo de texto para inserir o nome do dispositivo lopy.
     */
    private EditText editTextLopy;
    /**
     * Botão para se conectar ao dispositivo lopy.
     */
    private Button button_lopy;

    /**
     * Tag usada para identificar mensagens enviadas e recebidas pelo aplicativo.
     */
    //COmmunication between android and the house gateway(lopy)
    public static final String TAG = "msg";
    /**
     * URI do servidor MQTT.
     */
    public static final String serverUri = "tcp://broker.hivemq.com:1883";
    /**
     * Tópico de assinatura para receber mensagens do gateway da casa.
     */
    public static final String subscriptionTopic = "messagesFromCroatia/#";
    /**
     * Tópico de publicação para enviar mensagens para o gatewat da casa
     */
    public static final String publishTopic = "messagesFromCroatia";
    /**
     * Atributo que representa o identificador do cliente MQTT.
     * É composto por "MyAndroidClientId" concatenado com o timestamp atual.
     */
    private String clientId = "MyAndroidClientId" + System.currentTimeMillis();

    /**
     * Getter para o atributo textLogs
     * @return o valor do atributo textLogs
     */
    public TextView getTextLogs() {
        return textLogs;
    }

    /**
     * Setter para o atributo textLogs
     * @param textLogs o novo valor para o atributo textLogs
     */
    public void setTextLogs(TextView textLogs) {
        this.textLogs = textLogs;
    }

    /**
     * Atributo que representa a view de logs.
     */
    public static TextView textLogs;
    /**
     * Atributo que representa a view para edição de mensagem.
     */
    public EditText editTextMessage;
    /**
     * Atributo que representa o código de permissão utilizado.
     */
    private static final int PERMISSION_REQUEST_CODE = 100;

//    MqttManagerImpl mqttManager;

    //TTN CONNECT MQTT
    /**
     * Atributo que representa o cliente MQTT para conexão com o TTN.
     */
    MqttClient client_ttn;
    /**
     * Atributo para a classe de exemplo do Paho.
     */
    PahoDemo paho;


    /**
     * Método chamado quando o fragmento é iniciado
     * Ele sobrescreve o método onStart da classe pai.
     * Ele inicializa o gerenciador de casa, define o fragmento atual e o título da barra de ferramentas.
     * Ele também inicializa os campos de texto e o botão de submit.
     * Este método contém código comentado para conexão e publicação com o protocolo MQTT.
     */
    @Override
    public void onStart() {
        super.onStart();
        Context context = this.getActivity();
        houseManager = HouseManager.getInstance();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.app_name));

     
        textLogs = getView().findViewById(R.id.textLogs);
        editTextMessage = getView().findViewById(R.id.editTextMessage);

        //--------------------------MQTT---------------------
//        Mqtt3AsyncClient client = MqttClient.builder()
//                .useMqttVersion3()
//                .identifier("5c9496e0-58ff-11ed-bf0a-bb4ba43bd3f6")
//                .serverHost("mqtt.mydevices.com")
//                .serverPort(1883)
//                .sslWithDefaultConfig()
//                .buildAsync();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            client.connectWith()
//                    .simpleAuth()
//                    .username("411755b0-58fa-11ed-bf0a-bb4ba43bd3f6")
//                    .password("ea48065ad529cdc42764fc43645e4c10ef899ca4".getBytes())
//                    .applySimpleAuth()
//                    .send()
//                    .whenComplete((connAck, throwable) -> {
//                        if (throwable != null) {
//                            // handle failure
//                            System.out.println("Nao consegui conectar\n\n\n");
//                            System.out.println(throwable.getMessage());
//                            System.out.println(connAck.toString());
//                        } else {
//                            System.out.println("Consegui conectar\n\n\n");
//
//                            // setup subscribes or start publishing
//                        }
//                    });
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            client.publishWith()
//                    .topic("v1/411755b0-58fa-11ed-bf0a-bb4ba43bd3f6/things/5c9496e0-58ff-11ed-bf0a-bb4ba43bd3f6/data/3")
//                    .payload("digital_sensor,d=1".getBytes())
//                    .send()
//                    .whenComplete((publish, throwable) -> {
//                        if (throwable != null) {
//                            // handle failure to publish
//                            System.out.println("Nao consegui enviar\n\n\n");
//                            System.out.println(throwable.getMessage());
//                            System.out.println(publish.toString());
//                        } else {
//                            // handle successful publish, e.g. logging or incrementing a metric
//                            System.out.println("Consegui conectar\n\n\n");
//                        }
//                    });
//        }


        //textLogs.setText("oiiiiiiiiiiiiiiiiiiiiii");
//MQTT
        /*
        mqttManager = new MqttManagerImpl(
                getContext(),
                serverUri,
                clientId,
                new String[]{subscriptionTopic},
                new int[]{0}
        );*/
//        mqttManager.init();
//        initMqttStatusListener();
//        mqttManager.connect();
        Button buttonSubmit = getView().findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                SharedPreferences prefs = getActivity().getSharedPreferences("demopref",
//                        Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putString("demostring", "ola meros mortais");
//                editor.apply();
//                try {
//                    Thread.sleep(100);
//                    Log.d(TAG, prefs.getAll().toString());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }



//                String dados = editTextMessage.getText().toString();
//                    writeToFile(dados);
//
//
//
//                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.zsasko.mqttandroidsample");
//                if (launchIntent != null) {
//                    System.out.println("Eu vou iniciar o mundo\n\n\n\n\n\n\n");
//                    startActivity(launchIntent);//null pointer check in case package name was not found
//                }else{
//                    System.out.println("Eu sou null\n\n\n\n\n\n\n");
//                }


            //TTN TRY
            String message = editTextMessage.getText().toString();
            HouseManager.addString_send_ttn(1, message);
            PahoDemo.getInstance().submitMessage();

            }
            });


    }

    /**
     * Método onCreateView é chamado quando é necessário criar a exibição do fragmento.
     * Ele infla o layout do fragmento e o retorna.
     * @param inflater objeto LayoutInflater que pode ser usado para inflar o layout do fragmento
     * @param container objeto ViewGroup que contém o fragmento.
     * @param savedInstanceState objeto Bundle que contém o estado salvo do fragmento.
     * @return objeto View que contém o layout do fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    /**
     * Método writeToFile é usado para escrever uma String de dados em um arquivo no armazenamento externo.
     * Este método tenta escrever os dados no arquivo "mqtt_sharing.txt" dentro do diretório de documentos.
     * Ele exibe uma notificação Toast e imprime uma mensagem de log de sucesso ou falha.
     * @param data String de dados a ser escrita no arquivo.
     */
    private void writeToFile(String data) {
        /*try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("mqtt_SHARE.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/
        String filename ="mqtt_sharing.txt";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        try {
            FileOutputStream fos = new FileOutputStream(new File(dir,filename));
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(getActivity().getApplicationContext(),"Wrote to file: "+dir+filename,Toast.LENGTH_LONG).show();
            System.out.println("Wrote to file: "+dir+filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (!editTextMessage.getText().toString().isEmpty()) {
//            String state = Environment.getExternalStorageState();
//            if (Environment.MEDIA_MOUNTED.equals(state)) {
//                if (Build.VERSION.SDK_INT >= 23) {
//                    if (checkPermission()) {
//                        File sdcard = Environment.getExternalStorageDirectory();
//                        File dir = new File(sdcard.getAbsolutePath() + "/text/");
//                        dir.mkdir();
//                        File file = new File(dir, "sample.txt");
//                        FileOutputStream os = null;
//                        try {
//                            os = new FileOutputStream(file);
//                            os.write(editTextMessage.getText().toString().getBytes());
//                            os.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        requestPermission(); // Code for permission
//                    }
//                } else {
//                    File sdcard = Environment.getExternalStorageDirectory();
//                    File dir = new File(sdcard.getAbsolutePath() + "/text/");
//                    dir.mkdir();
//                    File file = new File(dir, "sample.txt");
//                    FileOutputStream os = null;
//                    try {
//                        os = new FileOutputStream(file);
//                        os.write(editTextMessage.getText().toString().getBytes());
//                        os.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    /**
     * Método checkPermission() verifica se a permissão de escrita em armazenamento externo foi concedida.
     * @return true se a permissão foi concedida e false caso contrário.
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
        return true;
        } else {
        return false;
        }
        }

    /**
     * Método requestPermission() solicita a permissão de escrita em armazenamento externo ao usuário. Exibe uma mensagem Toast
     * explicando a necessidade da permissão, caso o usuário ainda não tenha sido solicitado anteriormente.
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Toast.makeText(this.getActivity(), "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        }

    /**
     * Este método é chamado quando o resultado de uma solicitação de permissão é recebido.
     * Ele verifica se a permissão de escrita em armazenamento externo foi concedida ou negada pelo usuário.
     * @param requestCode O código da solicitação enviado anteriormente.
     * @param permissions O conjunto de permissões solicitadas.
     * @param grantResults O resultado da solicitação de permissão para cada permissão no conjunto de permissões.
     */
    @Override
public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
        case PERMISSION_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Log.e("value", "Permission Granted, Now you can use local drive .");
        } else {
        Log.e("value", "Permission Denied, You cannot use local drive .");
        }
        break;
        }
        }

    }

//
//    private void initMqttStatusListener() {
//        mqttManager.mqttStatusListener = new MqttStatusListener() {
//            @Override
//            public void onConnectComplete(boolean reconnect, String serverURI) {
//                if (reconnect) {
//                    displayInDebugLog("Reconnected to : " + serverURI);
//                } else {
//                    displayInDebugLog("Connected to: " + serverURI);
//                }
//            }
//
//            @Override
//            public void onConnectFailure(Throwable exception) {
//                displayInDebugLog("Failed to connect");
//            }
//
//            @Override
//            public void onConnectionLost(Throwable exception) {
//                displayInDebugLog("The Connection was lost.");
//            }
//
//            @Override
//            public void onMessageArrived(String topic, MqttMessage message) {
//                displayInMessagesList(new String(message.getPayload()));
//            }
//
//            @Override
//            public void onTopicSubscriptionSuccess() {
//                displayInDebugLog("Subscribed!");
//            }
//
//            @Override
//            public void onTopicSubscriptionError(Throwable exception) {
//                displayInDebugLog("Failed to subscribe");
//            }
//        };
//    }
//
//    private void displayInMessagesList(String message) {
//        textLogs.setText(message + "\n" + textLogs.getText());
//    }
//
//    private void displayInDebugLog(String message) {
//        Log.i(TAG, message);
//    }
//
//    private void submitMessage() {
//        System.out.println("Eu vou submeter a mensagem \n\n\n\n\n\n");
//        String message = editTextMessage.getText().toString();
//        if (TextUtils.isEmpty(message)) {
//            System.out.println("Eentrei no if \n\n\n\n\n\n");
//            displayToast(R.string.general_please_write_some_message);
//            return;
//        }
//        mqttManager.sendMessage(message, publishTopic);
//        clearInputField();
//    }
//
//    private void clearInputField() {
//        editTextMessage.setText("");
//    }
//
//    private void displayToast(@StringRes int messageId) {
//        Toast.makeText(this.getContext(), messageId, Toast.LENGTH_LONG).show();
//    }


