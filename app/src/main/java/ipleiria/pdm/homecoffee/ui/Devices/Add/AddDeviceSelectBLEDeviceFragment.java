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
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleBLEDevicesAdapter;
import ipleiria.pdm.homecoffee.components.LoadingDialog;
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;


public class AddDeviceSelectBLEDeviceFragment extends Fragment {
    public static final String CONFIG_SERVICE_UUID = "00000000-0000-0000-0000-000000000001";
    public static final String CHANNELS_CONFIG_CHARACTERISTIC_UUID = "c0000000-0000-0000-0000-000000000000";
    public static final String BLE_SERVER_NAME_CHARACTERISTIC_UUID = "c0000000-0000-0000-0000-000000000001";
    public static final String DEVICES_INFO_CHARACTERISTIC_UUID = "c0000000-0000-0000-0000-000000000002";

    public static final String DEVICE_MODE_SENSOR = "SENSOR";
    public static final String DEVICE_MODE_ACTUATOR = "ACTUATOR";

    public static final int CONFIG_OPERATIONS_NUMBER = 7;
    public static final int ATTEMPTS_MAX_NUM = 5;

    public static final String RESULT_BLE_SERVER_NAME = "RESULT_BLE_SERVER_NAME";
    public static final String RESULT_DEVICE_MODE = "RESULT_DEVICE_MODE";


    private static final int TIMEOUT = 10;
    private static final int WRITE_TIME_SLEEP = 1;
    private static final int MAX_RANDOM_INTEGER = 1000;

    private boolean discoverServicesSucceed = false;
    private boolean readCharacteristicsSucceed = false;
    private boolean writeCharacteristicsSucceed = false;
    private boolean deviceConnectionState = false;

    private boolean buttonPressed = false;
    private boolean continuePressed = false;

    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> devices;
    private List<BluetoothGattService> services;
    private BluetoothDevice selectedDevice;
    private BluetoothGattService selectedService;
    private BluetoothGatt gatt;

    LoadingDialog loadingDialog;

    private RecyclerView mRecyclerView;
    private RecycleBLEDevicesAdapter dAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ble_device_selection, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        MainActivity.addFragmentViseted(FragmentsEnum.SLIDES_HOW_FRAGMENT);
    }

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

    public void onBLEDevItemClick(int position){
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        selectedDevice = devices.get(position);

        connect(selectedDevice);
        if (!deviceConnectionState || selectedService == null){
            errorMessage();
            return;
        }
        List<BluetoothGattCharacteristic> characteristics = selectedService.getCharacteristics();

        BluetoothGattCharacteristic channelsConfigCharacteristic = null;
        BluetoothGattCharacteristic bleServerNameCharacteristic = null;
        BluetoothGattCharacteristic devicesInfoCharacteristic = null;
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            System.out.println("Characteristic: " + getUUID_toString(characteristic.getUuid()));
            System.out.println("\t" + characteristic.getUuid().toString());
            switch (characteristic.getUuid().toString()){
                case CHANNELS_CONFIG_CHARACTERISTIC_UUID:
                    channelsConfigCharacteristic = characteristic;
                    break;
                case BLE_SERVER_NAME_CHARACTERISTIC_UUID:
                    bleServerNameCharacteristic = characteristic;
                    break;
                case DEVICES_INFO_CHARACTERISTIC_UUID:
                    devicesInfoCharacteristic = characteristic;
                    break;
            }
        }


        int attemptsNum = 0;

        if (bleServerNameCharacteristic == null) {
            System.out.println("Target BLE Server Name characteristic not found");
            errorMessage();
            return;
        }else{
            attemptsNum = 0;
            String bleServerName = null;
            do{
                attemptsNum++;
                try{
                    bleServerName = readString(bleServerNameCharacteristic, "Devices Information");
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }while (bleServerName == null && attemptsNum < ATTEMPTS_MAX_NUM);
            HouseManager.addString_send_ttn(1, "RESULT_BLE_SERVER_NAME:" + bleServerName);
        }

        String value = null;
        if (devicesInfoCharacteristic == null) {
            System.out.println("Target Devices Information characteristic not found");
            errorMessage();
            return;
        }else{
            attemptsNum = 0;
            do{
                attemptsNum++;
                try{
                    value = readString(devicesInfoCharacteristic, "Devices Information");
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }while (value == null && attemptsNum < ATTEMPTS_MAX_NUM);
        }

        if(!readCharacteristicsSucceed){
            errorMessage();
            return;
        }

        String[] devicesDescriptions = value.split(",");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.txt_AlertDialog_AddDevicesTitle))
                        .setMessage(getResources().getString(R.string.txt_AlertDialog_AddDevices_Part1) +
                                devicesDescriptions.length + getResources().getString(R.string.txt_DevicesFragTitle) +
                                "\n\n" +
                                getResources().getString(R.string.txt_AlertDialog_AddDevices_Part2))
                        .setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                continuePressed = true;
                                buttonPressed = true;
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                buttonPressed = true;
                            }
                        })
                        .setIcon(R.drawable.add_icon)
                        .show();
            }
        });

        try {
            while(!buttonPressed){
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        if(!continuePressed){
            errorMessage();
            return;
        }

        StringBuilder channelsConfig = new StringBuilder();
        int nextChannelAvailable = HouseManager.getInstance().getDevices().size();

        for(int i = nextChannelAvailable; i < nextChannelAvailable + devicesDescriptions.length; i++){
            channelsConfig.append(i + ",");
        }

        if (channelsConfigCharacteristic == null) {
            System.out.println("Target Channels Configuration characteristic not found");
            errorMessage();
            return;
        }else{
            do{
                attemptsNum++;
                try {
                    writeString(channelsConfigCharacteristic, channelsConfig.toString(), "Advertising Name");
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }
            }while (!writeCharacteristicsSucceed && attemptsNum < ATTEMPTS_MAX_NUM);
        }

        if(!writeCharacteristicsSucceed){
            errorMessage();
            return;
        }

        int numSuccessfullyAddedDevices = 0;
        for (String devDescrip : devicesDescriptions){
            String[] descripValues = devDescrip.split(":");
            if(descripValues.length < 2){
                continue;
            }

            Device newDevice = null;

            DeviceType devType = DeviceType.searchByAcronym(descripValues[0]);
            if(devType == null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_AddDeviceFail),
                                Toast.LENGTH_LONG).show();
                    }
                });
                continue;
            }
            String principalDevMode = descripValues[1];
            Random random = new Random();
            if(principalDevMode.equals(DEVICE_MODE_SENSOR)){
                newDevice = new Sensor(
                        nextChannelAvailable,
                        "DEVICE #" + random.nextInt(1000),
                        devType,
                        HouseManager.getInstance().getRoom(0));
            }else if (principalDevMode.equals(DEVICE_MODE_ACTUATOR)){
                newDevice = new Actuator(
                        nextChannelAvailable,
                        "DEVICE #" + random.nextInt(1000),
                        devType,
                        HouseManager.getInstance().getRoom(0));
                if(descripValues.length == 3){
                    String auxDevMode = descripValues[2];
                    if(auxDevMode.equals(DEVICE_MODE_SENSOR)){
                        Sensor auxSensor = new Sensor(
                                nextChannelAvailable,
                                "DEVICE #" + random.nextInt(1000),
                                devType,
                                HouseManager.getInstance().getRoom(0));
//                        HouseManager.getInstance().getRoom(0).addDevice(auxSensor);
                        ((Actuator) newDevice).setAssociatedSensor(auxSensor);

                        if(HouseManager.getInstance().addDevice(auxSensor)){
                            numSuccessfullyAddedDevices++;
                        }else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),
                                            getResources().getString(R.string.toastMessage_AssociateSensorFail),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }
//            HouseManager.getInstance().getRoom(0).addDevice(newDevice);
            if(HouseManager.getInstance().addDevice(newDevice)){
                numSuccessfullyAddedDevices++;
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.toastMessage_AddDeviceFail),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            nextChannelAvailable++;
        }

        final int numSuccessfullyAddedDevices_Temp = numSuccessfullyAddedDevices;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), numSuccessfullyAddedDevices_Temp +
                                getResources().getString(R.string.toastMessage_DevicesAddedSuccessfully),
                        Toast.LENGTH_LONG).show();
            }
        });

        loadingDialog.dismisDialog();
        ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new DevicesFragment()).commit();
    }

    private void errorMessage(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.toastMessage_BLEDeviceConfigurationError),
                        Toast.LENGTH_LONG).show();
            }
        });
        loadingDialog.dismisDialog();
    }

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
            if (strUUID.equals(CONFIG_SERVICE_UUID) || service.getUuid().toString().equals(CONFIG_SERVICE_UUID)){
                selectedService = service;
            }
        }

        System.out.println(str.toString());

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