package ipleiria.pdm.homecoffee;

import static java.lang.Boolean.FALSE;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import org.eclipse.paho.client.mqttv3.MqttException;

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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.RoomType;
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.mqtt.PahoDemo;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;

public class HouseManager implements Serializable {
    private static HouseManager INSTANCE = null;

    private static Bundle bundle;

    private String GatewayBLEServerName;
    private String GatewayBLEServerDevEuiCode;

    private ArrayList<Room> rooms;
    private ArrayList<Device> devices;
    private ArrayList<Sensor> sensors;

    private User user;

    static final long serialVersionUID = 15L;

    private boolean loginMade=FALSE;

    //-------------------------------------- TTN ---------------------------------------------
    public static synchronized HashMap<Integer, String> getString_send_ttn() {
        return HouseManager.string_send_ttn;
    }

    public static void addString_send_ttn(int channel, String string_to_add){
        HouseManager.getString_send_ttn().put(channel,string_to_add);
    }

    private static HashMap<Integer, String> string_send_ttn=new HashMap<Integer, String>();



    public PahoDemo getPaho() {
        return paho;
    }

    private PahoDemo paho;

    public synchronized void submitMessage() {
        if (!string_send_ttn.isEmpty()) {

            paho.doDemo(string_send_ttn);
            string_send_ttn.clear();
        }

    }

    public void start_mqtt() {
        try {
            paho = new PahoDemo();
            paho.initDemo("v3/teste-rs2022@ttn/devices/eui-70b3d54990a17f82/up");
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nException: " + e.getMessage());
        }
    }




    public StringBuilder msgs_received = new StringBuilder();

    public StringBuilder getMsgs_received() {
        return msgs_received;
    }

    public void setMsgs_received(StringBuilder msgs_received) {
        this.msgs_received = msgs_received;
    }





    // ------------------------------------- Sensors -------------------------------------
    public void addSensor(Sensor sensor) {
        if (sensors.isEmpty() || !sensors.contains(sensor)) {
            sensors.add(sensor);
            Collections.sort(sensors);
        }
    }

    public void removeSensor(int pos) {
        sensors.remove(pos);
    }

    public Sensor getSensor(int pos) {
        return sensors.get(pos);
    }

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    public ArrayList<Sensor> searchSensors(String name) {
        ArrayList<Sensor> result = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if ((sensor.getName().toUpperCase()).contains(name.toUpperCase()))
                result.add(sensor);
        }
        return result;
    }

    public ArrayList<Sensor> searchSensors(DeviceType deviceType) {
        ArrayList<Sensor> result = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (sensor.getType() == deviceType)
                result.add(sensor);
        }
        return result;
    }

    // ------------------------------------- Devices -------------------------------------
    public boolean addDevice(Device device) {
        if (devices.isEmpty() || !devices.contains(device)) {
            devices.add(device);
            Collections.sort(devices);
            if(device instanceof Sensor){
                sensors.add((Sensor) device);
                Collections.sort(sensors);
            }
            return true;
        }
        return false;
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
    public Device searchDevicesChannel(int channel) {
        for (Device device : devices) {
            if(device.getChannel()==channel){
                return device;
            }
        }
        return null;
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

        Device dev1 = new Actuator(4, "Alarme de Temperatura", DeviceType.TEMPERATURE, initialRoom);
        Device dev2 = new Sensor(3, "Sensor de Temperatura", DeviceType.TEMPERATURE, initialRoom);
        Device dev3 = new Actuator(268, "Aquecedor", DeviceType.TEMPERATURE, initialRoom);
        Device dev4 = new Actuator(5, "Luminaria", DeviceType.LUMINOSITY, initialRoom);
        Device dev4_1 = new Sensor(5, "Sensor de Luminosidade", DeviceType.LUMINOSITY, initialRoom);
        Device dev5 = new Sensor(6, "Detetor de Chama", DeviceType.DIGITAL, initialRoom);
        Device dev6 = new Sensor(7, "Detetor de Proximidade", DeviceType.DIGITAL, initialRoom);
        Device dev7 = new Actuator(2, "Motor", DeviceType.DIGITAL, initialRoom);
        Device dev7_1 = new Sensor(2, "Sensor de Posição Motor", DeviceType.DIGITAL, initialRoom);
        Device dev8 = new Sensor(56, "Sensor de Humidade", DeviceType.HUMIDITY, initialRoom);
        Device dev9 = new Sensor(1, "Sensor de Gas", DeviceType.ANALOG, initialRoom);

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
        addDevice(dev6);
        addDevice(dev7);
        addDevice(dev8);
        addDevice(dev9);
        addDevice(dev4_1);
        addDevice(dev7_1);

        //generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d1 = calendar.getTime();
        // Points in different minutes
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -15);
        Date dm3 = calendar.getTime();
        calendar.add(Calendar.MINUTE, -24);
        Date dm2 = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date dm1 = calendar.getTime();

        DataPoint[] dataPoints1 = new DataPoint[] {
            new DataPoint(d1, 1),
            new DataPoint(d2, 5),
            new DataPoint(d3, 3),
            new DataPoint(d4, 2),

            new DataPoint(dm1, 10),
            new DataPoint(dm2, 6),
            new DataPoint(dm3, 1),

            new DataPoint(d5, 6)
        };
        for (int i = 0; i < dataPoints1.length; i++){
            dev2.getDataPoints().add(dataPoints1[i]);
        }
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
        sensors = new ArrayList<>();
        bundle = new Bundle();
    }

    public String getGatewayBLEServerName() {
        return GatewayBLEServerName;
    }

    public void setGatewayBLEServerName(String gatewayBLEServerName) {
        GatewayBLEServerName = gatewayBLEServerName;
    }

    public String getGatewayBLEServerDevEuiCode() {
        return GatewayBLEServerDevEuiCode;
    }

    public void setGatewayBLEServerDevEuiCode(String gatewayBLEServerDevEuiCode) {
        GatewayBLEServerDevEuiCode = gatewayBLEServerDevEuiCode;
    }

    public static synchronized Bundle getBundle() {
        return bundle;
    }

    public static synchronized void setBundle(Bundle bundle) {
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
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            error = true;
        } catch (IOException e) {
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            error = true;
        } catch (ClassNotFoundException e) {
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
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
