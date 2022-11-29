package ipleiria.pdm.homecoffee;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class HouseManager implements Serializable {
    private ArrayList<Room> rooms;
    private ArrayList<Device> devices;

    static final long serialVersionUID = 2L;

    // ------------------------------------- Devices -------------------------------------
    public void addDevice(Device device) {
        if (devices.isEmpty() || !devices.contains(device)) {
            devices.add(device);
            Collections.sort(devices);
        }
    }

    public void removeDevice(int pos) {
        devices.remove(pos);
    }

    public Device getDevice(int pos) {
        return devices.get(pos);
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public ArrayList<Device> searchDevices(String name) {
        ArrayList<Device> result = new ArrayList<>();
        for (Device device : devices) {
            if ((device.getName().toUpperCase()).contains(name.toUpperCase()))
                result.add(device);
        }
        return result;
    }

    public void saveDeviceConnectionState(){
        for (Device device : devices){
            device.setConnectionStateSaved(device.isConnectionState());
        }
    }

    public void recoverSavedDeviceConnectionState(){
        for (Device device : devices){
            device.setConnectionState(device.isConnectionStateSaved());
        }
    }

    public int numberOfDevicesConnect(){
        int devicesConnected = 0;
        for (Device device : devices){
            if (device.isConnectionState()){
                devicesConnected ++;
            }
        }
        return devicesConnected;
    }

    public void changeDevicesConnectionState(boolean connectionState){
        if (devices != null){
            for (Device device : devices){
                device.setConnectionState(connectionState);
            }
        }
    }

    public void addInitialDevices() {
        Room initialRoom;
        if (!rooms.isEmpty()){
            initialRoom = rooms.get(0);
        }else{
            initialRoom = new Room( "Sala",RoomType.LIVING_ROOM);
            rooms.add(initialRoom);
        }

        Device dev1 = new Device(125, "Sensor de Humidade", DeviceType.HUMIDITY, initialRoom);
        Device dev2 = new Device(456, "Sensor de Temperatura", DeviceType.TEMPERATURE, initialRoom);
        Device dev3 = new Device(789, "Sensor de Luminosidade", DeviceType.LIGHT, initialRoom);
        Device dev4 = new Device(852, "Sensor de   Pressão", DeviceType.PRESSURE, initialRoom);
        Device dev5 = new Device(159, "Sensor de Aceleração", DeviceType.ACCELERATION, initialRoom);
        addDevice(dev1);
        addDevice(dev2);
        addDevice(dev3);
        addDevice(dev4);
        addDevice(dev5);
    }

    //---------------------------------------------------
    //Código adicionado para garantir que há só uma instância da classe GestorContactos
    private static HouseManager INSTANCE = null;
    public static synchronized HouseManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HouseManager();
        }
        return INSTANCE;
    }
    private HouseManager() {
        rooms = new ArrayList<>();
        devices = new ArrayList<>();
    }
    //-----------------------------------------------------
    public void addRoom(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            Collections.sort(rooms);
        }
    }
    public void adicionarDadosIniciais() {
        Room c1 = new Room( "Sala",RoomType.LIVING_ROOM);
        Room c2 = new Room( "Cozinha",RoomType.KITCHEN);
        Room c3 = new Room( "Quarto",RoomType.BEDROOM);
        Room c4 = new Room( "Escritório",RoomType.OFFICE);
        Room c5 = new Room( "Casa de banho", RoomType.BATHROOM);
        addRoom(c1);
        addRoom(c2);
        addRoom(c3);
        addRoom(c4);
        addRoom(c5);
    }
    /*public void atualizarContacto(int pos, Room contacto) {
        if (!contactos.contains(contacto) || contacto.getNumero() ==
                contactos.get(pos).getNumero()) {
            contactos.set(pos, contacto);
            Collections.sort(contactos);
        }
    }*/
    public Room getRoom(int pos) {
        return rooms.get(pos);
    }
    public ArrayList<Room> getRooms() {
        return rooms;
    }
    public void removerContacto(int pos) {
        rooms.remove(pos);
    }
    public ArrayList<Room> procurarContacto(String nome) {
        ArrayList<Room> res = new ArrayList<>();
        for (Room c : rooms) {
            if ((c.getNome().toUpperCase()).contains(nome.toUpperCase()))
                res.add(c);
        }
        return res;
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rooms.size(); i++) {
            str.append(rooms.get(i)).append("\n");
        }
        return str.toString();
    }
    public static void gravarFicheiro(Context context) {
        try {
            FileOutputStream fileOutputStream =
                    context.openFileOutput("houseManager.bin", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new
                    ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(INSTANCE);
            objectOutputStream.writeObject(new Boolean(DevicesFragment.isDevicesEnable()));
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            Toast.makeText(context, "Could not write HouseManager to internal storage.", Toast.LENGTH_LONG).show();
        }
    }
    public static void lerFicheiro(Context context) {
        boolean error = false;
        try {
            FileInputStream fileInputStream =
                    context.openFileInput("houseManager.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            INSTANCE = (HouseManager) objectInputStream.readObject();
            Boolean DevicesEnable = (Boolean) objectInputStream.readObject();
            DevicesFragment.setDevicesEnable(DevicesEnable);
            objectInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Could not read HouseManager from internal storage.", Toast.LENGTH_LONG).show();
            error = true;
        } catch (IOException e) {
            Toast.makeText(context, "Error reading HouseManager from internal storage.", Toast.LENGTH_LONG).show();
            error = true;
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Error reading HouseManager from internal storage.", Toast.LENGTH_LONG).show();
            error = true;
        }

        if (error){
            INSTANCE = HouseManager.getInstance();
            INSTANCE.adicionarDadosIniciais();
            INSTANCE.addInitialDevices();
        }
    }
}
