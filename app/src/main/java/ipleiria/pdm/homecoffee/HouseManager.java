package ipleiria.pdm.homecoffee;

import static java.lang.Boolean.FALSE;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.RoomType;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;

public class HouseManager implements Serializable {
    private static HouseManager INSTANCE = null;

    private static Bundle bundle;

    private ArrayList<Room> rooms;
    private ArrayList<Device> devices;

    private User user;

    static final long serialVersionUID = 5L;

    private boolean loginMade=FALSE;

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
            initialRoom = new Room( "Sala", RoomType.LIVING_ROOM);
            rooms.add(initialRoom);
        }

        Device dev1 = new Device(125, "Sensor de Humidade", DeviceType.HUMIDITY, initialRoom);
        Device dev2 = new Device(456, "Sensor de Temperatura", DeviceType.TEMPERATURE, initialRoom);
        Device dev3 = new Device(789, "Sensor de Luminosidade", DeviceType.LIGHT, initialRoom);
        Device dev4 = new Device(852, "Sensor de   Pressão", DeviceType.PRESSURE, initialRoom);
        Device dev5 = new Device(159, "Sensor de Aceleração", DeviceType.ACCELERATION, initialRoom);

        dev1.addNotification(new Notification("Choveu!!!"));
        dev1.addNotification(new Notification(
                new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime(),
                "Falta agua!!!"));
        dev1.addNotification(new Notification(
                new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(),
                "Afogo-me!!!"));
        dev1.addNotification(new Notification(
                new GregorianCalendar(2022, Calendar.DECEMBER, 25).getTime(),
                "Tenho sede!!!"));

        addDevice(dev1);
        addDevice(dev2);
        addDevice(dev3);
        addDevice(dev4);
        addDevice(dev5);
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

    public int getRoomIndex(Room room){
        return rooms.indexOf(room);
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

    // ------------------------------------- House Manager -------------------------------------
    //Código adicionado para garantir que há só uma instância da classe GestorContactos
    public static synchronized HouseManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HouseManager();
        }
        return INSTANCE;
    }
    private HouseManager() {
        rooms = new ArrayList<>();
        devices = new ArrayList<>();
        bundle = new Bundle();


    }

    public static Bundle getBundle() {
        return bundle;
    }

    public static void setBundle(Bundle bundle) {
        HouseManager.bundle = bundle;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //---------------------LOGIN-------------------------

    public boolean isLoginMade() {
        return loginMade;
    }

    public void setLoginMade(boolean loginMade) {
        this.loginMade = loginMade;
    }


    //-------------------------------------------------
}
