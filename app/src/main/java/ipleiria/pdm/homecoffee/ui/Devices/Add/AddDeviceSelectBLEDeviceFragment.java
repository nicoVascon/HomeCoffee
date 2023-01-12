package ipleiria.pdm.homecoffee.ui.Devices.Add;

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
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleBLEDevicesAdapter;
import ipleiria.pdm.homecoffee.components.LoadingDialog;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

/**
 * Classe que representa um Fragmento para seleção de dispositivos BLE
 */
public class AddDeviceSelectBLEDeviceFragment extends Fragment {
    /**
     * UUID alvo do serviço BLE
     */
    public static final String TARGET_SERVICE_UUID = "0000000000000001";
    /**
     * UUID da característica do tipo de dispositivo BLE
     */
    public static final String DEVICE_TYPE_CHARACTERISTIC_UUID = "c000000000000001";
    public static final String ADVERT_NAME_CHARACTERISTIC_UUID = "c000000000000002";
    public static final String DEVICE_EUI_CODE_CHARACTERISTIC_UUID = "c000000000000003";
    public static final String APP_EUI_CODE_CHARACTERISTIC_UUID = "c000000000000004";
    public static final String APP_KEY_CODE_CHARACTERISTIC_UUID = "c000000000000005";
    public static final String BLE_SERVER_NAME_CHARACTERISTIC_UUID = "c000000000000006";
    public static final String TTN_APP_JOIN_STATE_CHARACTERISTIC_UUID = "c000000000000007";
    public static final String CONFIGURATION_STATE_CHARACTERISTIC_UUID = "c000000000000008";

    public static final String CONFIGURATION_READY_STATE = "CONFIGURATION READY";

    public static final int CONFIG_OPERATIONS_NUMBER = 7;

    public static final String RESULT_DEVICE_TYPE = "RESULT_DEVICE_TYPE";
    public static final String RESULT_DEVICE_MODE = "RESULT_DEVICE_MODE";

    private static final int TIMEOUT = 10;
    private static final int WRITE_TIME_SLEEP = 1;
    private static final int MAX_RANDOM_INTEGER = 1000;
    /**
     * Variável que indica se a descoberta de serviços BLE foi bem-sucedida
     */
    private boolean discoverServicesSucceed = false;
    /**
     * Variável que indica se a leitura de características BLE foi bem-sucedida
     */
    private boolean readCharacteristicsSucceed = false;
    /**
     * Variável que indica se a escrita de características BLE foi bem-sucedida
     */
    private boolean writeCharacteristicsSucceed = false;
    /**
     * Variável que indica o estado da conexão com o dispositivo BLE
     */
    private boolean deviceConnectionState = false;

    /**
     * Variável que indica se o botão foi pressionado
     */
    private boolean buttonPressed = false;
    /**
     * Variável que indica se a ação de continuar foi pressionada
     */
    private boolean continuePressed = false;

    /**
     * Adapter Bluetooth
     */
    private BluetoothAdapter bluetoothAdapter;
    /**
     * Lista de dispositivos BLE encontrados
     */
    private List<BluetoothDevice> devices;
    /**
     * Lista de serviços BLE encontrados
     */
    private List<BluetoothGattService> services;
    /**
     * Dispositivo BLE selecionado
     */
    private BluetoothDevice selectedDevice;
    /**
     * Serviço BLE selecionado
     */
    private BluetoothGattService selectedService;
    /**
     * GATT Bluetooth
     */
    private BluetoothGatt gatt;
    /**
     * Dialogo de carregamento
     */
    LoadingDialog loadingDialog;

    /**
     * RecyclerView de dispositivos BLE
     */
    private RecyclerView mRecyclerView;
    /**
     * Adaptador para o RecyclerView de dispositivos BLE
     */
    private RecycleBLEDevicesAdapter dAdapter;

    /**
     * Método chamado quando a visualização é criada
     * @param inflater inflador para inflar o layout do fragmento
     * @param container container onde o fragmento será adicionado
     * @param savedInstanceState estado salvo da instância (opcional)
     * @return a visualização criada
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ble_device_selection, container, false);
    }
    /**
     * Método chamado quando o fragmento é criado. Ele é usado para realizar inicializações de fragmentos, geralmente chamando super.onCreate() antes de realizar qualquer outra inicialização.
     * @param savedInstanceState um pacote de dados que contém o estado salvo do fragmento.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * Método chamado quando a visualização é destruída
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        MainActivity.addFragmentViseted(FragmentsEnum.SLIDES_HOW_FRAGMENT);
    }
    /**
     * Método chamado quando o fragmento é iniciado
     */
    @Override
    public void onStart() {
        super.onStart();
        /**
         * Recupera o adaptador Bluetooth padrão
         */
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt the user to enable it
        }
        /**
         * Recupera o botão de varredura BLE e adiciona um listener
         */
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
        /**
         * Inicializa a lista de dispositivos BLE e adiciona o adaptador de RecyclerView
         */
        devices = new ArrayList<>();
        mRecyclerView = getView().findViewById(R.id.recyclerViewDevices);
        dAdapter = new RecycleBLEDevicesAdapter(getActivity(), devices){
            /**
             * Método chamado quando um item da lista é clicado
             * @param position posição do item clicado na lista
             */
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
     * Called when a BLE device on the list is clicked.
     * It creates a new instance of LoadingDialog and starts the display, select the clicked BLE device from the devices list
     * and tries to connect it. If the connection fails or the selected service is null, the dialog is closed and the method returns.
     * Then it selects the specific BluetoothGattCharacteristic of the device (deviceTypeCharacteristic) according to its UUID.
     * If the device type characteristic is not found, it displays an error message.
     * If it is found, reads the value of this characteristic and adds this value to a bundle.
     * Finally, it displays the AddDeviceFragment fragment.
     * Closes the loading dialog.
     * @param position The position of the clicked device on the list
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

        BluetoothGattCharacteristic deviceTypeCharacteristic = null;
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            System.out.println("Characteristic: " + getUUID_toString(characteristic.getUuid()));
            switch (getUUID_toString(characteristic.getUuid())){
                case DEVICE_TYPE_CHARACTERISTIC_UUID:
                    deviceTypeCharacteristic = characteristic;
                    break;
            }
        }
        if (deviceTypeCharacteristic == null) {
            System.out.println("Target Device Type characteristic not found");
        }else{
            String value = readString(deviceTypeCharacteristic, "Device Type");
            if (readCharacteristicsSucceed){
                Bundle bundle = HouseManager.getBundle();
                if(bundle == null){
                    bundle = new Bundle();
                    HouseManager.setBundle(bundle);
                }
                String[] valueSplit = value.split(" ");
                if(valueSplit.length == 2){
                    bundle.putInt(AddDeviceFragment.RESULT_NEW_DEV_TYPE, DeviceType.valueOf(valueSplit[0]).ordinal());
                    bundle.putInt(AddDeviceFragment.RESULT_NEW_DEV_MODE, valueSplit[1] == "SENSOR"? 0:1);

                    ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container, new AddDeviceFragment()).commit();
                }
            }
        }

        loadingDialog.dismisDialog();
    }

    /**
     *O método é acionado quando um dispositivo da lista é selecionado.
     * Ele inicia uma caixa de diálogo de carregamento e se conecta ao dispositivo selecionado
     * Em seguida, procura as características relacionadas à configuração do dispositivo.
     * Se as características forem encontradas, ele lerá ou escreverá para elas, dependendo de sua função.
     * Ele manterá um contador do número de operações bem-sucedidas.
     * Se todas as operações forem bem-sucedidas, o fragmento será alterado para AddDeviceFragment.
     * @param position o índice do dispositivo selecionado na lista de dispositivos
     */
    public void Continue(int position){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        selectedDevice = devices.get(position);

        connect(selectedDevice);
        if (!deviceConnectionState || selectedService == null){
            loadingDialog.dismisDialog();
            return;
        }
        List<BluetoothGattCharacteristic> characteristics = selectedService.getCharacteristics();

        BluetoothGattCharacteristic deviceTypeCharacteristic = null;
        BluetoothGattCharacteristic advertNameCharacteristic = null;
        BluetoothGattCharacteristic devEuiCodeCharacteristic = null;
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
                case DEVICE_EUI_CODE_CHARACTERISTIC_UUID:
                    devEuiCodeCharacteristic = characteristic;
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

        int configOperationsCounter = 0;
        if (deviceTypeCharacteristic == null) {
            System.out.println("Target Device Type characteristic not found");
        }else{
            String value = readString(deviceTypeCharacteristic, "Device Type");
            if (readCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        if (advertNameCharacteristic == null) {
            System.out.println("Target Advertising Name characteristic not found");
        }else{
            String stringValue = "Oiii BLE";
            writeString(advertNameCharacteristic, stringValue, "Advertising Name");
            if (writeCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        String devEuiCode = null;
        if (devEuiCodeCharacteristic == null) {
            System.out.println("Target Device Eui Code characteristic not found");
        }else{
            devEuiCode = readString(devEuiCodeCharacteristic, "Device Eui Code");
            if (readCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        if (appEuiCodeCharacteristic == null) {
            System.out.println("Target App Eui Code characteristic not found");
        }else{
            String stringValue = "0000000000000111";
            writeString(appEuiCodeCharacteristic, stringValue, "App Eui Code");
            if (writeCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        if (appKeyCodeCharacteristic == null) {
            System.out.println("Target App Key Code characteristic not found");
        }else{
            String stringValue = "abc0000000000111";
            writeString(appKeyCodeCharacteristic, stringValue, "App Key Code");
            if(writeCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        String gatewayName = null;
        if (bleServerNameCharacteristic == null) {
            System.out.println("Target BLE Server Name characteristic not found");
        }else{
            Random random = new Random();
            gatewayName = "GATEWAY#" + random.nextInt(MAX_RANDOM_INTEGER);
            writeString(bleServerNameCharacteristic, gatewayName, "BLE Server Name");
            if(writeCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        if (ttnAppJoinStateCharacteristic == null) {
            System.out.println("Target TTN App Join State characteristic not found");
        }else{
            String value = readString(ttnAppJoinStateCharacteristic, "TTN App Join State");
            if(readCharacteristicsSucceed){
                configOperationsCounter++;
            }
        }

        if (configurationStateCharacteristic == null) {
            System.out.println("Target Configuration State State characteristic not found");
        }else{
            String value = readString(configurationStateCharacteristic, "Configuration State");
            int configOperationsCounter_temp = configOperationsCounter;
            String GateWayName = gatewayName;
            String GateWayEuiCode = devEuiCode;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(value.equals(CONFIGURATION_READY_STATE) && configOperationsCounter_temp == CONFIG_OPERATIONS_NUMBER){
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_BLEDeviceConfigurationSucceeded),
                                Toast.LENGTH_LONG).show();
                        HouseManager.getInstance().setGatewayBLEServerName(GateWayName);
                        HouseManager.getInstance().setGatewayBLEServerDevEuiCode(GateWayEuiCode);
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
     * Escaneia dispositivos BLE
     * Exibe um diálogo de carregamento enquanto os dispositivos são escaneados.
     * Verifica se a permissão de escaneamento do bluetooth foi concedida.
     * Obtém o scanner de dispositivos BLE do adaptador Bluetooth.
     * Caso o scanner não seja suportado pelo dispositivo, uma mensagem de erro é exibida.
     * Limpa a lista de dispositivos encontrados.
     * Utiliza uma callback para lidar com os resultados do escaneamento.
     * Verifica se a permissão de conexão do bluetooth foi concedida.
     * Adiciona o dispositivo encontrado à lista de dispositivos encontrados se ele ainda não estiver na lista e o número de dispositivos encontrados for menor que 40.
     * Notifica o adaptador da lista de dispositivos para atualizar a interface do usuário.
     * Se a lista de dispositivos encontrados tiver mais de 20 itens ou se o número total de dispositivos escaneados for igual a 40, o escaneamento é interrompido e o diálogo de carregamento é fechado.
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
     *
     * Método que é usado para ler uma característica do dispositivo BLE. Ele usa o método read(), para ler a característica desejada
     * @param targetCharacteristic característica desejada para ser lida.
     * @param characteristicName nome da característica desejada.
     * @return retorna o valor da característica lida em formato String.
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
     * Método que é usado para ler uma característica do dispositivo BLE.
     *
     * @param targetCharacteristic característica desejada para ser lida.
     * @return retorna o estado da operação de leitura (sucesso ou falha)
     */
    public void read(BluetoothGattCharacteristic targetCharacteristic) {
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
     * Escreve uma string em uma característica do dispositivo BLE.
     * @param targetCharacteristic a característica a ser escrita
     * @param stringValue o valor da string a ser escrito
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
     * Escreve em uma característica do dispositivo BLE
     *
     * @param targetCharacteristic a característica a ser escrita
     * @param value o valor a ser escrito
     */
    public void write(BluetoothGattCharacteristic targetCharacteristic, byte[] value){

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
     * Método que realiza a conexão com um dispositivo Bluetooth. Ele exibe uma mensagem de carregamento na tela, indicando
     * que a conexão está sendo estabelecida. Antes de tentar a conexão, é verificado se o dispositivo já não está conectado
     * e se o usuário possui permissão para se conectar a dispositivos Bluetooth. Em seguida, é chamado o método createBond
     * do objeto BluetoothDevice para iniciar o processo de emparelhamento. É criado um callback BluetoothGattCallback para
     * tratar os eventos de mudança de estado de conexão e descoberta de serviços. Caso a conexão seja estabelecida com
     * sucesso, uma mensagem é exibida na tela indicando que a conexão foi bem-sucedida e é iniciado o processo de
     * descoberta de serviços. Caso ocorra algum erro durante o processo de descoberta de serviços, uma mensagem é exibida
     * na tela indicando o erro.
     *
     * @param device O dispositivo Bluetooth com o qual se deseja se conectar.
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

        /**
         * Callback para mudanças de estado de conexão com o dispositivo Bluetooth
         */
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
     * Método que converte UUID em uma string legível
     * @param uuid UUID a ser convertido
     * @return String legível do UUID
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