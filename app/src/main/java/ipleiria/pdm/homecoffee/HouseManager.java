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

public class HouseManager implements Serializable {
    private ArrayList<Room> rooms;
    private ArrayList<Device> devices;

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

    public void addInitialDevices() {
        Device dev1 = new Device(125, "Sensor de Humidade", DeviceType.HUMIDITY,
                "imgs/humiditySensor.jpg");
        Device dev2 = new Device(456, "Sensor de Temperatura", DeviceType.TEMPERATURE,
                "imgs/temperatureSensor.jpg");
        Device dev3 = new Device(789, "Sensor de Luminosidade", DeviceType.LIGHT,
                "imgs/lightSensor.jpg");
        Device dev4 = new Device(852, "Sensor de Pressão", DeviceType.PRESSURE,
                "imgs/pressureSensor.jpg");
        addDevice(dev1);
        addDevice(dev2);
        addDevice(dev3);
        addDevice(dev4);
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
    public void adicionarContacto(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            Collections.sort(rooms);
        }
    }
    public void adicionarDadosIniciais() {
        Room c1 = new Room(21223344, "Francisco Correia", "");
        Room c2 = new Room(92323232, "Fernando Lima", "");
        Room c3 = new Room(91566677, "Vítor Damas", "");
        Room c4 = new Room(96765987, "Rui Jordão", "");
        Room c5 = new Room(24489056, "Ana Moreira", "");
        adicionarContacto(c1);
        adicionarContacto(c2);
        adicionarContacto(c3);
        adicionarContacto(c4);
        adicionarContacto(c5);
    }
    /*public void atualizarContacto(int pos, Room contacto) {
        if (!contactos.contains(contacto) || contacto.getNumero() ==
                contactos.get(pos).getNumero()) {
            contactos.set(pos, contacto);
            Collections.sort(contactos);
        }
    }*/
    public Room obterContacto(int pos) {

        return rooms.get(pos);
    }
    public ArrayList<Room> getContactos() {
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
    public void gravarFicheiro(Context context) {
        try {
            FileOutputStream fileOutputStream =
                    context.openFileOutput("rooms.bin", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new
                    ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(rooms);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            Toast.makeText(context, "Could not write GestorContactos to internal storage.", Toast.LENGTH_LONG).show();
        }
    }
    public void lerFicheiro(Context context) {
        try {
            FileInputStream fileInputStream =
                    context.openFileInput("contactos.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            rooms = (ArrayList<Room>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Could not read GestorContactos from internal storage.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error reading GestorContactos from internal storage.", Toast.LENGTH_LONG).show();

        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Error reading GestorContactos from internal storage.", Toast.LENGTH_LONG).show();
        }
    }
}
