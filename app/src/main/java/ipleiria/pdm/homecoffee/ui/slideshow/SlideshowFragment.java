package ipleiria.pdm.homecoffee.ui.slideshow;

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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleBLEDevicesAdapter;
import ipleiria.pdm.homecoffee.components.LoadingDialog;

/**
 * Classe que representa o fragmento Slideshow.
 * Possui constantes estáticas para os UUIDs dos características do dispositivo, como o tipo de dispositivo,
 * o nome de anúncio, o código EUI do dispositivo, o código EUI da aplicação, a chave da aplicação,
 * o nome do servidor BLE e o estado de junção da aplicação TTN. Também possui uma constante para o
 * estado de configuração do dispositivo e uma constante para o número de operações de configuração.
 */
public class SlideshowFragment extends Fragment {
    /**
     *  UUID do serviço alvo
     */
    public static final String TARGET_SERVICE_UUID = "0000000000000001";
    /**
     * UUID da característica do tipo de dispositivo
     */
    public static final String DEVICE_TYPE_CHARACTERISTIC_UUID = "c000000000000001";
    /**
     * UUID da característica do nome de anúncio
     */
    public static final String ADVERT_NAME_CHARACTERISTIC_UUID = "c000000000000002";
    /**
     * UUID da característica do código EUI do dispositivo
     */
    public static final String DEVICE_EUI_CODE_CHARACTERISTIC_UUID = "c000000000000003";
    /**
     * UUID da característica do código EUI da aplicação
     */
    public static final String APP_EUI_CODE_CHARACTERISTIC_UUID = "c000000000000004";
    /**
     * UUID da característica da chave da aplicação
     */
    public static final String APP_KEY_CODE_CHARACTERISTIC_UUID = "c000000000000005";
    /**
     * UUID da característica do nome do servidor BLE
     */
    public static final String BLE_SERVER_NAME_CHARACTERISTIC_UUID = "c000000000000006";
    /**
     * UUID da característica do estado de junção da aplicação TTN
     */
    public static final String TTN_APP_JOIN_STATE_CHARACTERISTIC_UUID = "c000000000000007";
    /**
     * UUID da característica do estado de configuração
     */
    public static final String CONFIGURATION_STATE_CHARACTERISTIC_UUID = "c000000000000008";

    /**
     * Estado de configuração pronto
     */
    public static final String CONFIGURATION_READY_STATE = "CONFIGURATION READY";
    /**
     * Número de operações de configuração
     */
    public static final int CONFIG_OPERATIONS_NUMBER = 7;

    /**
     * Variáveis de timeout e sleep para as operações de escrita
     */
    private static final int TIMEOUT = 10;
    private static final int WRITE_TIME_SLEEP = 1;
    /**
     * Variáveis booleanas para indicar o estado das operações de descobrimento de serviços, leitura e escrita de características
     * e o estado de conexão com o dispositivo BLE.
     */
    public boolean discoverServicesSucceed = false;
    public boolean readCharacteristicsSucceed = false;
    public boolean writeCharacteristicsSucceed = false;
    public boolean deviceConnectionState = false;

    /**
     * Atributo que armazena a instância do adaptador Bluetooth do dispositivo.
     */
    private BluetoothAdapter bluetoothAdapter;
    /**
     * Atributo que armazena uma lista de dispositivos Bluetooth pareados.
     */
    List<BluetoothDevice> devices;
    /**
     * Atributo que armazena uma lista de serviços disponíveis em dispositivos Bluetooth pareados.
     */
    List<BluetoothGattService> services;
    /**
     * Atributo que armazena o dispositivo Bluetooth selecionado pelo usuário.
     */
    BluetoothDevice selectedDevice;
    /**
     * Atributo que armazena o serviço Bluetooth selecionado pelo usuário.
     */
    BluetoothGattService selectedService;
    /**
     * Atributo que armazena a conexão GATT com o dispositivo Bluetooth selecionado.
     */
    BluetoothGatt gatt;
    /**
     * Atributo que armazena a referência a um diálogo de carregamento.
     */
    LoadingDialog loadingDialog;
    /**
     * Atributo que armazena a referência a um RecyclerView para exibir dispositivos pareados.
     */
    private RecyclerView mRecyclerView;
    /**
     * Atributo que armazena a referência ao adapter do RecyclerView que exibe dispositivos pareados.
     */
    private RecycleBLEDevicesAdapter dAdapter;

    /**
     * Método que é chamado quando a view do fragmento é criada.
     * @param inflater O objeto LayoutInflater responsável por inflar a view do fragmento.
     * @param container O container onde a view do fragmento será adicionada.
     * @param savedInstanceState O estado salvo da instância do fragmento.
     * @return A view do fragmento inflada.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slideshow, container, false);
    }

    /**
     * Método que é chamado quando o fragmento é criado.
     * @param savedInstanceState O estado salvo da instância do fragmento.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Método que é chamado quando a view do fragmento é destruída.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.SLIDES_HOW_FRAGMENT);
    }

    /**
     * Método que é chamado quando o fragmento é iniciado.
     * Ele verifica a disponibilidade do BLE no dispositivo e configura um botão para iniciar a busca de dispositivos BLE.
     */
    @Override
    public void onStart() {
        super.onStart();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt the user to enable it
        }

        Button btnScanBLE = getView().findViewById(R.id.btnScanBLE);
        btnScanBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.txt_AlertDialog_TTNEndDeviceRegistrationTitle));
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.alertdialog_gwconfig_layout, (ViewGroup) getView(), false);
                // Set up the input
                TextView textViewAlertDialogGWConfig = viewInflated.findViewById(R.id.textViewAlertDialogGWConfig);
                textViewAlertDialogGWConfig.setText(getResources().getString(R.string.txt_AlertDialog_TTNEndDeviceRegistration) +
                        "\n\n" + getResources().getString(R.string.txt_DeviceEUICode) + "value");
                final EditText EditTextGWConfigAppEUI = (EditText) viewInflated.findViewById(R.id.EditTextGWConfigAppEUI);
                final EditText EditTextGWConfigAppKey = (EditText) viewInflated.findViewById(R.id.EditTextGWConfigAppKey);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);
                builder.setIcon(R.drawable.link_icon);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
                        String appEui = EditTextGWConfigAppEUI.getText().toString();
                        String appKey = EditTextGWConfigAppKey.getText().toString();
                        System.out.println("String!!!!!!!!!!!!!!!!  AppEui: " + appEui + " AppKey: " + appKey);
                        if (appEui == null || appEui.trim().isEmpty() || appKey == null || appKey.trim().isEmpty()){
                            Toast.makeText(getContext(), getResources().getString(R.string.toastMessage_RemainingFields),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
     * Método que exibe um diálogo de carregamento e o fecha após 5 segundos.
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
     * Método que é chamado quando um item de dispositivo BLE é clicado na lista de dispositivos pareados.
     * Ele exibe um diálogo de carregamento, seleciona o dispositivo selecionado, conecta-se a ele e exibe suas características.
     * @param position A posição do item clicado na lista de dispositivos pareados.
     */
    public void onBLEDevItemClick(int position){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
//        loadingDialog.setMainText(getResources().getString(R.string.txt_loadingDialog_ConnectingToDevice));
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

        if (devEuiCodeCharacteristic == null) {
            System.out.println("Target Device Eui Code characteristic not found");
        }else{
            String value = readString(devEuiCodeCharacteristic, "Device Eui Code");
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

        if (bleServerNameCharacteristic == null) {
            System.out.println("Target BLE Server Name characteristic not found");
        }else{
            String stringValue = "BLE Server :)";
            writeString(bleServerNameCharacteristic, stringValue, "BLE Server Name");
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(value.equals(CONFIGURATION_READY_STATE) && configOperationsCounter_temp == CONFIG_OPERATIONS_NUMBER){
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_BLEDeviceConfigurationSucceeded),
                                Toast.LENGTH_LONG).show();
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
     * Método que dá scan aos dispositivos BLE e confirma se tem permissão ou não para o fazer
     * Adiciona os dispositivos encontrados
     *
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

    public void writeString(BluetoothGattCharacteristic targetCharacteristic, String stringValue, String characteristicName){
        byte[] value = stringValue.getBytes(StandardCharsets.UTF_8);
        write(targetCharacteristic, value);
        if (writeCharacteristicsSucceed){
            loadingDialog.setSubText(getResources().getString(R.string.txt_loadingDialog_Characteristic_Part1) +
                    characteristicName +
                    getResources().getString(R.string.txt_loadingDialog_CharacteristicWroten_Part2));
        }
    }

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