package ipleiria.pdm.homecoffee;

import static java.lang.Boolean.FALSE;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;

public class HouseManager implements Serializable , Cloneable{

    static final long serialVersionUID = 23L;

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
    private ArrayList<Actuator> actuators;

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
            }else{
                actuators.add((Actuator) device);
                Collections.sort(actuators);
            }
            return true;
        }
        return false;
    }

    public void removeDevice(int position) {
        Device device = devices.get(position);
        removeDevice(device);
    }

    public void removeDevice(Device device) {
//        HouseManager.getInstance().searchRoomByDevice(device).getDevices().remove(device);
        device.getRoom().removeDevice(device);
        devices.remove(device);
        if(device instanceof Sensor){
            sensors.remove(device);
        }else{
            actuators.remove(device);
        }
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
    public void saveActuatorValue(){
        for (Actuator actuator : actuators){
            actuator.setConnectionStateSaved(actuator.isConnectionState());
        }
    }

    public void recoverSavedActuatorValue(){
        for (Actuator actuator : actuators){
            actuator.setConnectionState(actuator.isConnectionStateSaved());
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

        //generate Dates
//        Calendar calendar = Calendar.getInstance();
//        Date d5 = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date d4 = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date d3 = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date d2 = calendar.getTime();
//        calendar.add(Calendar.DATE, -1);
//        Date d1 = calendar.getTime();
//        // Points in different minutes
//        calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, -15);
//        Date dm3 = calendar.getTime();
//        calendar.add(Calendar.MINUTE, -24);
//        Date dm2 = calendar.getTime();
//        calendar.add(Calendar.MINUTE, -10);
//        Date dm1 = calendar.getTime();
//
//        DataPoint[] dataPoints1 = new DataPoint[] {
//            new DataPoint(d1, 1),
//            new DataPoint(d2, 5),
//            new DataPoint(d3, 3),
//            new DataPoint(d4, 2),
//
//            new DataPoint(dm1, 10),
//            new DataPoint(dm2, 6),
//            new DataPoint(dm3, 1),
//
//            new DataPoint(d5, 6)
//        };
//        for (int i = 0; i < dataPoints1.length; i++){
//            dev2.getDataPoints().add(dataPoints1[i]);
//        }
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
            Map<String, Object> room_map = new HashMap<>();
            //room.put("User_Email", userMail);
            room_map.put("Room_Name", room.getRoom_Name());
            room_map.put("Room_Type", room.getRoom_Type());
//            room_map.put("Sensors", room.getSensors());
//            room_map.put("Actuators", room.getSensors());
            HouseManager.getInstance().getUser().getRoomsRef().document(room.getRoom_Name()).set(room_map);

        }
    }

    public void removeRoom(Room room) {
        if (!rooms.contains(room)) {
            return;
        }

        HouseManager.getInstance().getUser().getRoomsRef().document(room.getRoom_Name()).delete();
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
                                                    room.addLocalDevice(sensor);

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
                                            if (!task.isSuccessful()) {
                                                return;
                                            }
                                            for (DocumentSnapshot deviceSnapshot : task.getResult()) {
                                                Actuator actuator = deviceSnapshot.toObject(Actuator.class);

                                                if(actuator.getAssociateddSensorRef() != null){
                                                    actuator.getAssociateddSensorRef()
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Sensor sensor = task.getResult().toObject(Sensor.class);
//                                                                            Actuator actuator = deviceSnapshot.toObject(Actuator.class);


                                                                        sensor.getAssociatedRoomRef()
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Room room_sensor = task.getResult().toObject(Room.class);

                                                                                            sensor.set_Room(room_sensor);

                                                                                            actuator.setAssociatedSensor(sensor);

                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                                addDevice(actuator);
                                                room.addLocalDevice(actuator);
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
        actuators = new ArrayList<>();
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
//            INSTANCE.adicionarDadosIniciais();
//            INSTANCE.addInitialDevices();
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
