package ipleiria.pdm.homecoffee.ui.Devices.Gateaway;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleBLEDevicesAdapter;
import ipleiria.pdm.homecoffee.components.LoadingDialog;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

/**
 * Classe responsável por gerenciar a seleção de dispositivos BLE e configurações de dispositivos BLE.
 * Possui constantes de UUIDs de características e serviços, estados de configuração, constantes de tempo,
 * variáveis para armazenar informações sobre dispositivos BLE e objetos Bluetooth para gerenciamento de conexão.
 */
public class GWConfig_BLEDeviceSelectionFragment extends Fragment {
    /**
     * UUID do serviço alvo
     */
    public static final String TARGET_SERVICE_UUID = "0000000000000001";
    /**
     * UUID da característica do tipo de dispositivo
     */
    public static final String DEVICE_TYPE_CHARACTERISTIC_UUID = "c000000000000001";
    /**
     * UUID da característica do nome do anúncio
     */
    public static final String ADVERT_NAME_CHARACTERISTIC_UUID = "c000000000000002";
    /**
     * UUID da característica do User ID
     */
    public static final String USER_ID_CHARACTERISTIC_UUID = "c000000000000003";
    /**
     * UUID da característica do código EUI da aplicação
     */
    public static final String APP_EUI_CODE_CHARACTERISTIC_UUID = "c000000000000004";
    /**
     * UUID da caracteristica da chave de acesso da aplicação
     */
    public static final String APP_KEY_CODE_CHARACTERISTIC_UUID = "c000000000000005";
    /**
     * UUID da caracteristica do nome do servidor BLE
     */
    public static final String BLE_SERVER_NAME_CHARACTERISTIC_UUID = "c000000000000006";
    /**
     * UUID da caracteristica que diz o estado da conexão entre a app e o TTN
     */
    public static final String TTN_APP_JOIN_STATE_CHARACTERISTIC_UUID = "c000000000000007";
    /**
     * UUID da caracteristica que diz o estado da configuração
     */
    public static final String CONFIGURATION_STATE_CHARACTERISTIC_UUID = "c000000000000008";

    /**
     * Representa o estado de configuração pronta
     */
    public static final String CONFIGURATION_READY_STATE = "CONFIGURATION READY";
    /**
     * Representa o estado de conexão com TTN estabelecida
     */
    public static final String TTN_JOINED_STATE = "JOINED";
    /**
     * Representa o estado de conexão com TTN não estabelecida
     */
    public static final String TTN_NOT_JOINED_STATE = "NOT JOINED";
    /**
     * Representa o número máximo de operações de configuração
     */
    public static final int CONFIG_OPERATIONS_NUMBER = 7;

    /**
     * Representa o tempo limite de espera
     */
    private static final int TIMEOUT = 10;
    /**
     * Representa o tempo de espera entre escrita de características
     */
    private static final int WRITE_TIME_SLEEP = 1;
    /**
     * Representa o número máximo de inteiro aleatório
     */
    private static final int MAX_RANDOM_INTEGER = 1000;
    /**
     * Representa o tempo de espera para conexão com o TTN
     */
    private static final int TTN_JOIN_TIME_SLEEP = 3000;

    private static final int ATTEMPS_MAX_NUM = 5;

    /**
     * Atributo booleano que indica se a descoberta de serviços foi bem sucedida
     */
    public boolean discoverServicesSucceed = false;
    /**
     * Atributo booleano que indica se a leitura de características foi bem sucedida
     */
    public boolean readCharacteristicsSucceed = false;
    /**
     * Atributo booleano que indica se a escrita de características foi bem sucedida
     */
    public boolean writeCharacteristicsSucceed = false;
    /**
     * Atributo booleano que indica se o dispositivo está conectado
     */
    public boolean deviceConnectionState = false;

    /**
     * Atributo booleano que indica se o botão foi pressionado
     */
    private boolean buttonPressed = false;
    /**
     * Atributo booleano que indica se o botão de continuar foi pressionado
     */
    private boolean continuePressed = false;
    /**
     * Atributo inteiro que conta as operações de configuração
     */
    private int configOperationsCounter;
    /**
     * Atributo do tipo BluetoothAdapter que representa o adaptador Bluetooth do dispositivo
     */
    private BluetoothAdapter bluetoothAdapter;
    /**
     * Lista de dispositivos Bluetooth encontrados
     */
    private List<BluetoothDevice> devices;
    /**
     * Lista de serviços Bluetooth Gatt encontrados
     */
    private List<BluetoothGattService> services;
    /**
     * Dispositivo Bluetooth selecionado
     */
    private BluetoothDevice selectedDevice;
    /**
     * Serviço Bluetooth Gatt selecionado
     */
    private BluetoothGattService selectedService;
    /**
     * Atributo do tipo BluetoothGatt que representa a conexão GATT com o dispositivo
     */
    private BluetoothGatt gatt;
    /**
     * Diálogo de carregamento
     */
    private LoadingDialog loadingDialog;
    /**
     * Atributo do tipo RecyclerView que exibe uma lista de dispositivos Bluetooth
     */
    private RecyclerView mRecyclerView;
    /**
     * Adaptador para a lista de dispositivos Bluetooth
     */
    private RecycleBLEDevicesAdapter dAdapter;

    /**
     * Código EUI da aplicação
     */
    private String appEUICode;

    /**
     * Código de acesso á aplicação
     */
    private String appKeyCode;

    /**
     * Código que identifica ao gateway
     */
    private String devEuiCode;

    /**
     * Método chamado quando é criado o fragmento. Ele infla o layout e inicializa os componentes da view.
     * @param inflater O inflater que vai inflar o layout
     * @param container O container onde o layout será adicionado
     * @param savedInstanceState O estado salvo da instância
     * @return View criada para o fragmento.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ble_device_selection, container, false);
    }

    /**
     * Método chamado quando o fragmento é criado. Ele inicializa o adaptador Bluetooth e verifica se o Bluetooth está habilitado
     * @param savedInstanceState - Bundle contendo o estado salvo da instância anterior do fragmento.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Método chamado quando a view do fragmento é destruída. Ele adiciona o fragmento a uma lista de visitados
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.SLIDES_HOW_FRAGMENT);
    }

    /**
     * Método chamado quando o fragmento é iniciado. Ele inicializa os componentes da view e configura os eventos de clique
     */
    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_GWConfigTitle));

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt the user to enable it
        }

        Button btnScanBLE = getView().findViewById(R.id.btnScanBLE);
        btnScanBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDevice = null;
                selectedService = null;
                gatt = null;
                scanLeDevice();
            }
        });

        devices = new ArrayList<>();
        mRecyclerView = getView().findViewById(R.id.recyclerViewDevices);
        dAdapter = new RecycleBLEDevicesAdapter(getActivity(), devices){
            @Override
            public void onItemClick(View view, int position) {
                new Thread(new Runnable() {
                    public void run() {
                        // a potentially time consuming task
                        onBLEDevItemClick(position);
                    }
                }).start();

            }
        };
        mRecyclerView.setAdapter(dAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * Método que cria uma caixa de diálogo de carregamento com uma duração de 5 segundos
     */
    public void openLoadingDialog()
    {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismisDialog();
            }
        },5000); //You can change this time as you wish
    }

    /**
     * Método que é chamado quando um dispositivo BLE é selecionado na lista de dispositivos. Ele conecta ao dispositivo selecionado, lê características e inicia a configuração do dispositivo.
     *
     * @param position Posição do dispositivo BLE selecionado na lista de dispositivos.
     */
    public void onBLEDevItemClick(int position){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        selectedDevice = devices.get(position);

        connect(selectedDevice);
        if (!deviceConnectionState || selectedService == null){
            loadingDialog.dismisDialog();
            return;
        }
        List<BluetoothGattCharacteristic> characteristics = selectedService.getCharacteristics();

        BluetoothGattCharacteristic userIDCharacteristic = null;
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            System.out.println("Characteristic: " + getUUID_toString(characteristic.getUuid()));
            switch (getUUID_toString(characteristic.getUuid())){
                case USER_ID_CHARACTERISTIC_UUID:
                    userIDCharacteristic = characteristic;
                    break;
            }
        }
        configOperationsCounter = 0;
        if (userIDCharacteristic == null) {
            System.out.println("Target Device Eui Code characteristic not found");
            loadingDialog.dismisDialog();
        }else{
//            devEuiCode = readString(userIDCharacteristic, "User ID");
            int attemptsNum = 0;
            do{
                attemptsNum++;
                try {
                    String stringValue = HouseManager.getInstance().getUser().getId();
                    writeString(userIDCharacteristic, stringValue, "User ID");
                    if (writeCharacteristicsSucceed){
                        configOperationsCounter++;
                    }
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }
            }while (!writeCharacteristicsSucceed && attemptsNum < ATTEMPS_MAX_NUM);
            if(!writeCharacteristicsSucceed){
                loadingDialog.dismisDialog();
                return;
            }
            if (writeCharacteristicsSucceed){
                configOperationsCounter++;
                loadingDialog.dismisDialog();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(buttonPressed && continuePressed){
                            return;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(getResources().getString(R.string.txt_AlertDialog_TTNEndDeviceRegistrationTitle));
                        // I'm using fragment here so I'm using getView() to provide ViewGroup
                        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.alertdialog_gwconfig_layout, (ViewGroup) getView(), false);
                        // Set up the input
                        TextView textViewAlertDialogGWConfig = viewInflated.findViewById(R.id.textViewAlertDialogGWConfig);
                        textViewAlertDialogGWConfig.setText(getResources().getString(R.string.txt_AlertDialog_TTNEndDeviceRegistration));
                        final EditText EditTextGWConfigAppEUI = (EditText) viewInflated.findViewById(R.id.EditTextGWConfigAppEUI);
                        final EditText EditTextGWConfigAppKey = (EditText) viewInflated.findViewById(R.id.EditTextGWConfigAppKey);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        builder.setView(viewInflated);
                        builder.setIcon(R.drawable.link_icon);

                        // Set up the buttons
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appEUICode = EditTextGWConfigAppEUI.getText().toString();
                                appKeyCode = EditTextGWConfigAppKey.getText().toString();
                                System.out.println("String!!!!!!!!!!!!!!!!  AppEui: " + appEUICode + " AppKey: " + appKeyCode);
                                if (appEUICode == null || appEUICode.trim().isEmpty() || appKeyCode == null || appKeyCode.trim().isEmpty()){
                                    Toast.makeText(getContext(), getResources().getString(R.string.toastMessage_RemainingFields),
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                continuePressed = true;
                                buttonPressed = true;
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                buttonPressed = true;
                            }
                        });

                        builder.show();
                    }
                });

                try {
                    while(!buttonPressed){
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }

                if(continuePressed){
                    Continue(position);
                }
            }
        }
    }

    /**
     * O método Continue é responsável por continuar o processo de configuração do dispositivo BLE selecionado.
     * Ele é chamado após o usuário pressionar o botão "Continue" na tela de seleção de dispositivo BLE.
     * @param position Posição do dispositivo BLE selecionado na lista de dispositivos encontrados.
     */
    public void Continue(int position){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog(true);

        selectedDevice = devices.get(position);

        connect(selectedDevice);
        if (!deviceConnectionState || selectedService == null){
            loadingDialog.dismisDialog();
            return;
        }
        List<BluetoothGattCharacteristic> characteristics = selectedService.getCharacteristics();

        BluetoothGattCharacteristic deviceTypeCharacteristic = null;
        BluetoothGattCharacteristic advertNameCharacteristic = null;
        BluetoothGattCharacteristic appEuiCodeCharacteristic = null;
        BluetoothGattCharacteristic appKeyCodeCharacteristic = null;
        BluetoothGattCharacteristic bleServerNameCharacteristic = null;
        BluetoothGattCharacteristic ttnAppJoinStateCharacteristic = null;
        BluetoothGattCharacteristic configurationStateCharacteristic = null;
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            System.out.println("Characteristic: " + getUUID_toString(characteristic.getUuid()));
            switch (getUUID_toString(characteristic.getUuid())){
                case DEVICE_TYPE_CHARACTERISTIC_UUID:
                    deviceTypeCharacteristic = characteristic;
                    break;
                case ADVERT_NAME_CHARACTERISTIC_UUID:
                    advertNameCharacteristic = characteristic;
                    break;
                case APP_EUI_CODE_CHARACTERISTIC_UUID:
                    appEuiCodeCharacteristic = characteristic;
                    break;
                case APP_KEY_CODE_CHARACTERISTIC_UUID:
                    appKeyCodeCharacteristic = characteristic;
                    break;
                case BLE_SERVER_NAME_CHARACTERISTIC_UUID:
                    bleServerNameCharacteristic = characteristic;
                    break;
                case TTN_APP_JOIN_STATE_CHARACTERISTIC_UUID:
                    ttnAppJoinStateCharacteristic = characteristic;
                    break;
                case CONFIGURATION_STATE_CHARACTERISTIC_UUID:
                    configurationStateCharacteristic = characteristic;
                    break;
            }
        }

        int attemptsNum = 0;
        if (deviceTypeCharacteristic == null) {
            System.out.println("Target Device Type characteristic not found");
        }else{
            String value = readString(deviceTypeCharacteristic, "Device Type");
            if (readCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }
        attemptsNum = 0;
        Random random = new Random();
        String gatewayName = "GATEWAY#" + random.nextInt(MAX_RANDOM_INTEGER);
        if (advertNameCharacteristic == null) {
            System.out.println("Target Advertising Name characteristic not found");
        }else{
            do{
                attemptsNum++;
                try {
                    writeString(advertNameCharacteristic, gatewayName, "Advertising Name");
                    if (writeCharacteristicsSucceed){
                        configOperationsCounter++;
                    }
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }
            }while (!writeCharacteristicsSucceed && attemptsNum < ATTEMPS_MAX_NUM);
        }

        if (appEuiCodeCharacteristic == null) {
            System.out.println("Target App Eui Code characteristic not found");
        }else{
            attemptsNum = 0;
            do{
                attemptsNum++;
                try {
                    String stringValue = appEUICode;
                    writeString(appEuiCodeCharacteristic, stringValue, "App Eui Code");
                    if (writeCharacteristicsSucceed){
                        configOperationsCounter++;
                    }
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }
            }while (!writeCharacteristicsSucceed && attemptsNum < ATTEMPS_MAX_NUM);
        }

        if (appKeyCodeCharacteristic == null) {
            System.out.println("Target App Key Code characteristic not found");
        }else{
            attemptsNum = 0;
            do{
                attemptsNum++;
                try {
                    String stringValue = appKeyCode.length() <= 16? appKeyCode: appKeyCode.substring(0, 16);
                    writeString(appKeyCodeCharacteristic, stringValue, "App Key Code");
                    if(writeCharacteristicsSucceed){
                        configOperationsCounter++;
                    }
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }
            }while (!writeCharacteristicsSucceed && attemptsNum < ATTEMPS_MAX_NUM);
        }

        if (bleServerNameCharacteristic == null) {
            System.out.println("Target BLE Server Name characteristic not found");
        }else{
            writeString(bleServerNameCharacteristic, gatewayName, "BLE Server Name");
            if(writeCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        if (ttnAppJoinStateCharacteristic == null) {
            System.out.println("Target TTN App Join State characteristic not found");
        }else{
            attemptsNum = 0;
            String value = TTN_NOT_JOINED_STATE;
            do{
                attemptsNum++;
                try{
                    value = readString(ttnAppJoinStateCharacteristic, "TTN App Join State");
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }while ((value == null || !value.equals(TTN_JOINED_STATE)) && attemptsNum < ATTEMPS_MAX_NUM);
            if(readCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        if (configurationStateCharacteristic == null) {
            System.out.println("Target Configuration State State characteristic not found");
        }else{
            attemptsNum = 0;
            String value = null;
            do{
                attemptsNum++;
                try{
                    value = readString(configurationStateCharacteristic, "Configuration State");
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }while ((value == null || !value.equals(CONFIGURATION_READY_STATE)) && attemptsNum < ATTEMPS_MAX_NUM);

            int configOperationsCounter_temp = configOperationsCounter;
            String GateWayName = gatewayName;
//            String GateWayEuiCode = devEuiCode;
            String value_2 = value;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(value_2 == null || value_2.equals(CONFIGURATION_READY_STATE)){
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_BLEDeviceConfigurationSucceeded),
                                Toast.LENGTH_LONG).show();
                        HouseManager.getInstance().setGatewayBLEServerName(GateWayName);
//                        HouseManager.getInstance().setGatewayBLEServerDevEuiCode(GateWayEuiCode);
                        ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().
                                replace(R.id.fragment_container, new DevicesFragment()).commit();
                    }else{
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_BLEDeviceConfigurationError),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        loadingDialog.dismisDialog();
    }

    /**
     * Método scanLeDevice() é usado para escanear dispositivos BLE (Bluetooth Low Energy) disponíveis.
     * Ele mostra uma caixa de diálogo de carregamento enquanto os dispositivos são escaneados. Se o aplicativo não tiver permissão para escanear dispositivos BLE, ele exibirá uma mensagem de erro.
     * Ele usa a classe BluetoothLeScanner para escanear os dispositivos e uma instância da classe ScanCallback para lidar com os resultados do escaneamento.
     * Ao encontrar um dispositivo, ele é adicionado à lista de dispositivos encontrados e o adaptador da lista é notificado para atualizar a exibição.
     */
    private void scanLeDevice() {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        loadingDialog.setMainText(getResources().getString(R.string.txt_loadingDialog_ScanningDevices));
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Não tem permissão!!");
            return;
        }
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            System.out.println("Bluetooth LE scanning is not supported on this device");
        }

        devices.clear();
        ScanCallback scanCallback = new ScanCallback() {
            public int devicesDetected = 0;

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Oiiiiiiiii Não tem permissão...................");
                    return;
                }
                devicesDetected++;
                System.out.println("oiiiiiiiiiii Outro: " + devicesDetected);

                // Add the device to the list of found devices
                if (!devices.contains(result.getDevice()) && devicesDetected < 40) {
                    devices.add(result.getDevice());
                    System.out.println("Oiiiii Deu..........");
                    System.out.println("Device Name: " + result.getDevice().getName());
                    dAdapter.notifyDataSetChanged();
                }
                if (devices.size() > 20 || devicesDetected == 40) {
                    System.out.println("-------------------- Oi Stop bluetoothLeScanner!!!!!");
                    bluetoothLeScanner.stopScan(this);
                    loadingDialog.dismisDialog();
                }
            }
        };
        bluetoothLeScanner.startScan(scanCallback);
    }

    /**
     * Método para ler uma string de um característica Bluetooth Gatt.
     * @param targetCharacteristic Característica Bluetooth Gatt a ser lida.
     * @param characteristicName Nome da característica sendo lida.
     * @return Retorna o valor lido da característica em formato de string.
     */
    public String readString(BluetoothGattCharacteristic targetCharacteristic, String characteristicName){
        read(targetCharacteristic);
        //        int intValue = targetCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
//        float value = Float.intBitsToFloat(intValue);
        byte[] bytes = targetCharacteristic.getValue();
        if (bytes != null) {
            String value = new String(bytes, StandardCharsets.UTF_8);
            System.out.println("oiiiiiiii deu: " + value);
            loadingDialog.setSubText(getResources().getString(R.string.txt_loadingDialog_Characteristic_Part1) +
                    characteristicName +
                    getResources().getString(R.string.txt_loadingDialog_CharacteristicRead_Part2));
            return value;
        }
        return null;
    }

    /**
     * Método para ler um característica Bluetooth Gatt.
     * @param targetCharacteristic Característica Bluetooth Gatt a ser lida.
     */
    public void read(BluetoothGattCharacteristic targetCharacteristic) {
        if(!deviceConnectionState){
            connect(selectedDevice);
            targetCharacteristic = selectedService.getCharacteristic(targetCharacteristic.getUuid());
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Oiiiiiiiii Não tem permissão...................");
            return;
        }
        readCharacteristicsSucceed = false;
        if (!gatt.readCharacteristic(targetCharacteristic)) {
            System.out.println("Read operation failed. Characteristic UUID: " + getUUID_toString(targetCharacteristic.getUuid()));
        }

        try {
            int i = 0;
            while(!readCharacteristicsSucceed && i < TIMEOUT){
                System.out.println("Ainda Não. Segundo: " + i);
                Thread.sleep(1000);
                i++;
            }
            if (i == TIMEOUT){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_CharacteristicReadOperationFailed),
                                Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Método que escreve uma string em uma característica específica do dispositivo conectado via Bluetooth.
     * @param targetCharacteristic característica do dispositivo a qual a string será escrita.
     * @param stringValue string a ser escrita na característica.
     * @param characteristicName nome da característica a qual a string será escrita.
     */
    public void writeString(BluetoothGattCharacteristic targetCharacteristic, String stringValue, String characteristicName){
        byte[] value = stringValue.getBytes(StandardCharsets.UTF_8);
        write(targetCharacteristic, value);
        if (writeCharacteristicsSucceed){
            loadingDialog.setSubText(getResources().getString(R.string.txt_loadingDialog_Characteristic_Part1) +
                    characteristicName +
                    getResources().getString(R.string.txt_loadingDialog_CharacteristicWroten_Part2));
        }
    }

    /**
     * Método que escreve um valor em uma característica específica do dispositivo conectado via Bluetooth.
     * @param targetCharacteristic característica do dispositivo a qual o valor será escrito.
     * @param value valor a ser escrito na característica.
     */
    public void write(BluetoothGattCharacteristic targetCharacteristic, byte[] value){
        if(!deviceConnectionState){
            connect(selectedDevice);
            targetCharacteristic = selectedService.getCharacteristic(targetCharacteristic.getUuid());
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Oiiiiiiiii Não tem permissão...................");
            return;
        }

//        float floatValue = Float.parseFloat(editTextNumberDecimal.getText().toString()); // the float value you want to write
//        int intValue = Float.floatToIntBits(floatValue);
        //String stringValue = editTextNumberDecimal.getText().toString();// the string value you want to write

        //targetCharacteristic.setValue(intValue, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        targetCharacteristic.setValue(value);
        writeCharacteristicsSucceed = false;
        if (!gatt.writeCharacteristic(targetCharacteristic)) {
            System.out.println("Writing the characteristic failed Characteristic UUID: " + getUUID_toString(targetCharacteristic.getUuid()));
        }

        try {
            int i = 0;
            while(!writeCharacteristicsSucceed && i < WRITE_TIME_SLEEP){
                System.out.println("Ainda Não. Segundo: " + i);
                Thread.sleep(1000);
                i++;
            }
            if (i == TIMEOUT){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_CharacteristicWriteOperationFailed),
                                Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *Método para conectar-se a um dispositivo Bluetooth. Ele exibe uma caixa de diálogo de carregamento com o texto "Conectando ao dispositivo" enquanto tenta conectar-se ao dispositivo.
     * @param device O dispositivo Bluetooth ao qual deseja-se conectar.
     * Caso o dispositivo já esteja conectado, o método retorna sem fazer nada.
     * Verifica se a permissão BLUETOOTH_CONNECT foi concedida, caso contrário, imprime uma mensagem de erro e retorna.
     * Tenta criar um vínculo entre o dispositivo e o dispositivo do usuário. Se a tentativa falhar, imprime uma mensagem de erro e retorna.
     * Registra um callback BluetoothGattCallback para gerenciar mudanças de estado na conexão. Caso a conexão seja bem-sucedida, o método discoverServices é chamado.
     * Caso a conexão seja perdida, uma mensagem Toast é exibida informando o usuário.
     */
    public void connect(BluetoothDevice device) {
        loadingDialog.setMainText(getResources().getString(R.string.txt_loadingDialog_ConnectingToDevice));

        if (deviceConnectionState){
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Oiiiiiiiii Não tem permissão...................");
            return;
        }
        boolean success = device.createBond();
        if (!success) {
            System.out.println("Create Bonding failed");
            return;
        }

        BluetoothGattCallback callback = new BluetoothGattCallback(){
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // The device is now connected
                    System.out.println("BluetoothGattCallback: The device is now connected");
                    deviceConnectionState = true;

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        System.out.println("BluetoothGattCallback: Oiiiiiiiii Não tem permissão...................");
                        return;
                    }

                    loadingDialog.setSubText(getResources().getString(R.string.txt_loadingDialog_ConnectionWithDeviceSucceed) + device.getName());

                    if (gatt.discoverServices()) {
                        System.out.println("BluetoothGattCallback: discoverServices Succeeded!!!");
                    } else {
                        System.out.println("BluetoothGattCallback: discoverServices Fail!!!");
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // The device is now disconnected
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getResources().getString(R.string.toastMessage_BLEDeviceDisconnected),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    System.out.println("BluetoothGattCallback: The device is now disconnected");
                    deviceConnectionState = false;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // Services have been discovered
                    System.out.println("BluetoothGattCallback: Services have been discovered");
                    discoverServicesSucceed = true;

                    loadingDialog.setSubText(getResources().getString(R.string.txt_loadingDialog_ServicesDiscovered));

                    services = gatt.getServices();
                    System.out.println("BluetoothGattCallbackv2: Services Size: " + services.size() + "-----------");
                } else {
                    // Service discovery failed, handle the error
                    System.out.println("BluetoothGattCallback: Service discovery failed, handle the error");
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    readCharacteristicsSucceed = true;
                    System.out.println("Read operation has been completed successfully");
                } else {
                    System.out.println("Read operation failed!!!");
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    writeCharacteristicsSucceed = true;
                    System.out.println("Write operation has been completed successfully");
                } else {
                    System.out.println("Write operation failed!!!");
                }
            }
        };
        gatt = device.connectGatt(getContext(), false, callback);

        try {
            int i = 0;
            while(!deviceConnectionState && i < TIMEOUT){
                System.out.println("Ainda Não. Segundo: " + i);
                Thread.sleep(1000);
                i++;
            }
            if (!deviceConnectionState && i == TIMEOUT){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_BLEDeviceConnectionFailed),
                                Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            // Handle the exception
        }

        try {
            int i = 0;
            while(!discoverServicesSucceed && i < TIMEOUT){
                System.out.println("Ainda Não. Segundo: " + i);
                Thread.sleep(1000);
                i++;
            }
            if (i == TIMEOUT){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_BLEDiscoverServicesFailed),
                                Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            // Handle the exception
        }

        StringBuilder str = new StringBuilder();
        str.append("Serviços:\n");
        for (BluetoothGattService service : services){
            str.append("\nUUID Received: " + service.getUuid().toString());
            String strUUID = getUUID_toString(service.getUuid());
            str.append("\n" + strUUID);
            if (strUUID.equals(TARGET_SERVICE_UUID)){
                selectedService = service;
            }
        }

        if (selectedService == null) {
            System.out.println("Target service not found");
            return;
            // Target service not found, handle the error
        }
        loadingDialog.setSubText(getResources().getString(R.string.txt_loadingDialog_ConfigurationServiceFound));

    }

    /**
     * Este método é utilizado para converter um UUID (Universally Unique Identifier) em uma string.
     * Ele primeiramente remove os traços presentes no UUID, depois percorre o código UUID em dois em dois caracteres,
     * convertendo-os em um valor inteiro hexadecimal e então convertendo este valor inteiro em um caractere.
     * O resultado é um string com caracteres que representam o UUID original.
     * @param uuid Uma UUID a ser convertida em string
     * @return Retorna uma string representando o UUID passado como argumento
     */
    public String getUUID_toString(UUID uuid){
        String uuidCode = uuid.toString().replace("-", "");
        StringBuilder strUUID = new StringBuilder();
        for (int i = 0; i < uuidCode.length(); i += 2) {
            int hexRep = Integer.parseInt(uuidCode.substring(i, i + 2), 16);
            char c = (char) hexRep;
            strUUID.append(c);
        }
        strUUID.reverse();
        return strUUID.toString();
    }

}