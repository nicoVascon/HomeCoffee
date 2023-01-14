package ipleiria.pdm.homecoffee;

import static java.lang.Boolean.FALSE;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
import java.util.HashMap;
import java.util.Map;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.RoomType;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.components.LoadingDialog;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;

public class HouseManager implements Serializable , Cloneable{

    static final long serialVersionUID = 20L;

    public static boolean gettingUserRooms;
    public static boolean userRoomsRefGotten;

    private static HouseManager INSTANCE = null;
    private static Bundle bundle;
    private static boolean modificable;

    private static User user;

    private String GatewayBLEServerName;
    private String GatewayBLEServerDevEuiCode;

    private ArrayList<Room> rooms;
    private ArrayList<Device> devices;
    private ArrayList<Sensor> sensors;

    private boolean loginMade = false;

    private boolean usersRoomDone = false;
    private boolean roomRemove = FALSE;

    private int color_back_rooms=R.color.iconBackgoundRooms;

    //-------------------------------------- TTN ---------------------------------------------
    public static synchronized HashMap<Integer, String> getString_send_ttn() {
        return HouseManager.string_send_ttn;
    }

    public static void addString_send_ttn(int channel, String string_to_add) {
        HouseManager.getString_send_ttn().put(channel, string_to_add);
    }

    private static HashMap<Integer, String> string_send_ttn = new HashMap<Integer, String>();

    public static StringBuilder msgs_received = new StringBuilder();

    public StringBuilder getMsgs_received() {
        return msgs_received;
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
        if (device != null && (devices.isEmpty() || !devices.contains(device))) {
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

    public void removeDevice(int position) {
        Device device = devices.get(position);
        HouseManager.getInstance().searchRoomByDevice(device).getDevices().remove(device);
        devices.remove(position);
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
    public Sensor searchSensorByChannel(int channel) {
        for (Sensor sensor : sensors) {
            if(sensor.getChannel()==channel){
                return sensor;
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
        if(this.rooms.isEmpty()){
            this.addRoom(new Room( "Sala (P/ TESTE)", RoomType.LIVING_ROOM));
        }

        Room initialRoom = rooms.get(0);

        Device dev1 = new Actuator(4, "Alarme de Temperatura", DeviceType.TEMPERATURE, initialRoom);
        Device dev2 = new Sensor(3, "Sensor de Temperatura", DeviceType.TEMPERATURE, initialRoom);
        ((Actuator) dev1).setAssociatedSensor((Sensor) dev2);
        Device dev3 = new Actuator(268, "Aquecedor", DeviceType.TEMPERATURE, initialRoom);
        Device dev4 = new Actuator(5, "Luminaria", DeviceType.LUMINOSITY, initialRoom);
        Device dev4_1 = new Sensor(5, "Sensor de Luminosidade", DeviceType.LUMINOSITY, initialRoom);
        ((Actuator) dev4).setAssociatedSensor((Sensor) dev4_1);
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

    }

    //---------------------ROOM--------------------------------


    public boolean isRoomRemove() {
        return roomRemove;
    }

    public void setRoomRemove(boolean roomRemove) {
        this.roomRemove = roomRemove;
    }

    public void addRoom(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            Collections.sort(rooms);
        }
    }

    public void removeRoom(Room room) {
        if (rooms.contains(room)) {
//            String room_name=room.getRoom_Name();
//            //Saving to Firebase
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            String userMail = HouseManager.getInstance().getUser().getEmail();
//            CollectionReference usersRef = db.collection("users");

            HouseManager.getInstance().getUser().getRoomsRef().document(room.getRoom_Name()).delete();

//            HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).collection("Actuators").document(String.valueOf(dev.getChannel())).set(dev);
//
//            Query query = usersRef.whereEqualTo("User_Email", userMail);
//            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot result = task.getResult();
//                        if (!result.isEmpty()) {
//                            DocumentSnapshot userDoc = result.getDocuments().get(0);
//                            CollectionReference roomsRef = userDoc.getReference().collection("rooms");
//                            Query query = roomsRef.whereEqualTo("Room_Name", room_name);
//                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        QuerySnapshot roomsSnapshot = task.getResult();
//                                        if (!roomsSnapshot.isEmpty()) {
//                                            DocumentSnapshot roomDoc = roomsSnapshot.getDocuments().get(0);
//                                            roomDoc.getReference().delete();
//                                        }
//
//                                    }
//                                }
//                            });
//                        }
//                    }
//
//
//                }
//
//            });


        }


        rooms.remove(room);
        Collections.sort(rooms);
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

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

//    public void getUserRooms(RecycleRoomsAdapter adapter,LoadingDialog loadingDialog) {
//        HouseManager.modificable = false;
//
//        //If this function is done getting users's rooms or not
//        ArrayList<Room> rooms_user = new ArrayList<>();
//        this.devices.clear();
//        this.sensors.clear();
//        this.rooms.clear();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String userMail = user.getEmail();
//        CollectionReference usersRef = db.collection("users");
//        Query query = usersRef.whereEqualTo("User_Email", userMail);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot result = task.getResult();
//                    if (!result.isEmpty()) {
//                        DocumentSnapshot userDoc = result.getDocuments().get(0);
//                        CollectionReference roomsRef = userDoc.getReference().collection("rooms");
//                        roomsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    ArrayList<Device> receivedSensors = new ArrayList<>();
//
//                                    QuerySnapshot roomsSnapshot = task.getResult();
//                                    for (DocumentSnapshot roomSnapshot : roomsSnapshot) {
//                                        Map<String, Object> map = roomSnapshot.getData();
//                                        System.out.println("Map: " + map.toString());
//                                        String name = (String) map.get("Room_Name");
//                                        String typeName = (String) map.get("Room_Type");
//                                        RoomType type = RoomType.valueOf(typeName);
//                                        Room newRoom = new Room(name, type);
//                                        ArrayList<Map> devicesMap = (ArrayList<Map>) map.get("Devices");
//                                        for(Map devMap : devicesMap){
//                                            Device newDevice = null;
//
//                                            String nameDev = (String) devMap.get("name");
//                                            Long channelDev = (Long) devMap.get("channel");
//                                            String typeDevName = (String) devMap.get("type");
//                                            DeviceType typeDev = DeviceType.valueOf(typeDevName);
//                                            ArrayList<Notification> notifications = (ArrayList<Notification>) devMap.get("notifications");
//                                            ArrayList<DataPointImpl> dataPoints = (ArrayList<DataPointImpl>) devMap.get("dataPoints");
//                                            double value = (Double) devMap.get("value");
//                                            double valueSaved = (Double) devMap.get("valueSaved");
//                                            boolean connectionState = (Boolean) devMap.get("connectionState");
//                                            boolean connectionStateSaved = (Boolean) devMap.get("connectionStateSaved");
//                                            if(devMap.containsKey("associatedSensor")){
//                                                Map<String, Object> associateSensorMap = (Map<String, Object>) devMap.get("associatedSensor");
//                                                newDevice = new Actuator(Integer.parseInt(String.valueOf(channelDev)), nameDev, typeDev, newRoom);
//                                                if(associateSensorMap.containsKey("channel")){
//                                                    Sensor sensorToAssociate = HouseManager.getInstance().searchSensorByChannel(
//                                                            (Integer) associateSensorMap.get("channel"));
//                                                    if(sensorToAssociate == null){
//                                                        String nameSensorToAssociate = (String) associateSensorMap.get("name");
//                                                        int channelSensorToAssociate = (Integer) associateSensorMap.get("channel");
//                                                        String typeSensorToAssociateName = (String) associateSensorMap.get("type");
//                                                        DeviceType typeSensorToAssociate = DeviceType.valueOf(typeSensorToAssociateName);
//                                                        ArrayList<Notification> notificationsSensorToAssociate = (ArrayList<Notification>) associateSensorMap.get("notifications");
//                                                        ArrayList<DataPointImpl> dataPointsSensorToAssociate = (ArrayList<DataPointImpl>) associateSensorMap.get("dataPoints");
//                                                        double valueSensorToAssociate = (Double) associateSensorMap.get("value");
//                                                        double valueSavedSensorToAssociate = (Double) associateSensorMap.get("valueSaved");
//                                                        boolean connectionStateSensorToAssociate = (Boolean) associateSensorMap.get("connectionState");
//                                                        boolean connectionStateSavedSensorToAssociate = (Boolean) associateSensorMap.get("connectionStateSaved");
//
//                                                        sensorToAssociate = new Sensor(channelSensorToAssociate, nameSensorToAssociate, typeSensorToAssociate,
//                                                                newRoom);
//                                                        sensorToAssociate.setValue(valueSensorToAssociate);
//                                                        sensorToAssociate.setValueSaved(valueSavedSensorToAssociate);
//                                                        sensorToAssociate.setNotifications(notificationsSensorToAssociate);
//                                                        sensorToAssociate.setDataPoints(dataPointsSensorToAssociate);
//                                                        sensorToAssociate.setConnectionState(connectionStateSensorToAssociate);
//                                                        sensorToAssociate.setConnectionStateSaved(connectionStateSavedSensorToAssociate);
//                                                        HouseManager.getInstance().addDevice(sensorToAssociate);
//                                                    }
//                                                    ((Actuator) newDevice).setAssociatedSensor(sensorToAssociate);
//                                                }
//                                            }else{
//                                                newDevice = new Sensor(Integer.parseInt(String.valueOf(channelDev)), nameDev, typeDev, newRoom);
//                                            }
//
//                                            if(newDevice != null){
//                                                newDevice.setValue(value);
//                                                newDevice.setValueSaved(valueSaved);
//                                                newDevice.setNotifications(notifications);
//                                                newDevice.setDataPoints(dataPoints);
//                                                newDevice.setConnectionState(connectionState);
//                                                newDevice.setConnectionStateSaved(connectionStateSaved);
//                                                HouseManager.getInstance().addDevice(newDevice);
//                                            }
//                                        }
//
//                                        HouseManager.getInstance().addRoom(newRoom);
////                                        System.out.println(room.toString());
////                                        //addRoom(room);
////                                        rooms_user.add(room);
//
//                                    }
//
////                                    HouseManager.getInstance().setRooms(rooms_user);
//                                    HouseManager.getInstance().addInitialDevices();
//                                    adapter.notifyDataSetChanged();
//                                    try {
//                                        loadingDialog.dismisDialog();
//                                    }catch (Exception e){
//                                        // Exception Raised when the data downloading finish so fast. Can be ignored.
//                                    }
////                                    loadingDialog.dismisDialog();
//                                    HouseManager.modificable = true;
//                                }
//                            }
//                        });
//                    }
//                }
//
//
//            }
//
//        });
//    }

    public void getUserRooms(LoadingDialog loadingDialog) {
        //If this function is done getting users's rooms or not
        rooms.clear();
        devices.clear();
        sensors.clear();
        user.getRoomsRef().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        QuerySnapshot roomsSnapshot = task.getResult();
                        for (DocumentSnapshot roomSnapshot : roomsSnapshot) {
                            Room room = roomSnapshot.toObject(Room.class);
                            System.out.println(room.toString());
                            //addRoom(room);
                            addRoom(room);
                            for(Device device : room.getDevices()){
                                addDevice(device);
                            }
                        }
//                        HouseManager.getInstance().setRooms(userRooms);
//                        HouseManager.getInstance().addInitialDevices();
                        loadingDialog.dismisDialog();
                    }
                }catch (Exception e){
                    System.out.println("Exception: getUserRooms: " + e.getMessage());
                }
            }
        });
    }

    public void getUserRooms(RecycleRoomsAdapter adapter,LoadingDialog loadingDialog) {
        //If this function is done getting users's rooms or not
        rooms.clear();
        devices.clear();
        sensors.clear();
        user.getRoomsRef().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        QuerySnapshot roomsSnapshot = task.getResult();
                        for (DocumentSnapshot roomSnapshot : roomsSnapshot) {
                            Room room = roomSnapshot.toObject(Room.class);
                            System.out.println(room.toString());
                            //addRoom(room);
                            addRoom(room);
//                            for(Device device : room.getDevices()){
//                                addDevice(device);
//                                room.addDevice(device);
//                            }

                            // Retrieve the devices collection for the current room
                            roomSnapshot.getReference().collection("Sensors")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot deviceSnapshot : task.getResult()) {
                                                    Sensor sensor = deviceSnapshot.toObject(Sensor.class);
                                                    addDevice(sensor);
                                                    room.addDevice(sensor);
                                                    sensor.set_Room(room);
                                                }
                                            }
                                        }
                                    });

                            // Retrieve the devices collection for the current room
                            roomSnapshot.getReference().collection("Actuators")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot deviceSnapshot : task.getResult()) {
                                                    Actuator actuator = deviceSnapshot.toObject(Actuator.class);
                                                    addDevice(actuator);
                                                    room.addDevice(actuator);
                                                }
                                            }
                                        }
                                    });

                        }
//                        HouseManager.getInstance().setRooms(userRooms);
//                        HouseManager.getInstance().addInitialDevices();
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismisDialog();
                    }
                }catch (Exception e){
                    System.out.println("Exception: getUserRooms: " + e.getMessage());
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(loadingDialog.getActivity(),R.string.toastMessage_NoRooms,Toast.LENGTH_SHORT).show();
                    }
                });


                loadingDialog.dismisDialog();

            }
        });

    }

    public Room searchRoomByDevice(Device device){

        for(Room room : rooms){
            ArrayList<Device> devices= room.getDevices();
            for(Device dev : devices){
                if(dev.equals(device)){
                    return room;
                }
            }
        }
        return null;
    }





    public int getColor_back_rooms() {
        return color_back_rooms;
    }

    public void setColor_back_rooms(int color_back_rooms) {
        this.color_back_rooms = color_back_rooms;
    }

    public boolean isUsersRoomDone() {
        return usersRoomDone;
    }

    public void setUsersRoomDone(boolean usersRoomDone) {
        this.usersRoomDone = usersRoomDone;
    }

    // ------------------------------------- House Manager -------------------------------------
    //Código adicionado para garantir que há só uma instância da classe HouseManager
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

    public static boolean isModificable() {
        return modificable;
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
        HouseManager.modificable = false;
        try {
            FileOutputStream fileOutputStream =
                    context.openFileOutput("houseManager.bin", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new
                    ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(HouseManager.getInstance().clone());
            objectOutputStream.writeObject(new Boolean(DevicesFragment.isDevicesEnable()));
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException | CloneNotSupportedException e) {
            Toast.makeText(context, "Could not write HouseManager to internal storage.", Toast.LENGTH_LONG).show();
        }
        HouseManager.modificable = true;
    }
    public static void lerFicheiro(Context context) {
        HouseManager.modificable = false;
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
        HouseManager.modificable = true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        HouseManager.userRoomsRefGotten = false;
        this.user = user;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users").document(user.getEmail()).collection("rooms");

        HouseManager.getInstance().getUser().setRoomsRef(usersRef);
        HouseManager.userRoomsRefGotten=true;

    }

    public void setCurrentUser(FirebaseUser currentUser) {
        String user_mail = currentUser.getEmail();
        User user = new User(user_mail);
        setUser(user);
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
