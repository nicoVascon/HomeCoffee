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

/**
 * Classe HouseManager implementa a interface Serializable e Cloneable
 *
 * É responsável por gerenciar as informações de uma casa, como as salas, dispositivos e sensores.
 * Possui atributos privados estáticos para instância única, bundle, nome do servidor BLE, código de dispositivo eui do servidor BLE,
 * listas de salas, dispositivos e sensores.
 * Possui métodos para adicionar e remover salas, dispositivos e sensores, buscar por salas, dispositivos e sensores
 * pelo nome e tipo de dispositivo, salvar e recuperar o estado de conexão dos dispositivos, mudar o estado de conexão dos dispositivos,
 * verificar o número de dispositivos conectados e adicionar dispositivos iniciais.
 */
public class HouseManager implements Serializable , Cloneable{
    /**
     * Variável estática para garantir a compatibilidade entre as versões da classe durante a serialização.
     */
    static final long serialVersionUID = 23L;
    /**
     * Variavel "flag" que verifica se os dados ja foram buscados
     */
    public static boolean gettingUserRooms;
    /**
     * Variavel "flag" que verifica que a referencia dos quartos ja foi buscada
     */
    public static boolean userRoomsRefGotten;
    /**
     * Variável estática que armazena a única instância da classe.
     */
    private static HouseManager INSTANCE = null;
    /**
     * Variável estática que armazena o bundle.
     */
    private static Bundle bundle;
    /**
     * Variavel "flag" que verifica que se o housemanager é modificável
     */
    private static boolean modificable;
    /**
     * Variável que armazena o usuário.
     */
    private static User user;
    /**
     * Variável que armazena o nome do servidor BLE do gateway.
     */
    private String GatewayBLEServerName;
    /**
     * Variável que armazena o código de EUI do servidor BLE do gateway.
     */
    private String GatewayBLEServerDevEuiCode;
    /**
     * Variável que armazena uma lista de objetos Room.
     */
    private ArrayList<Room> rooms;
    /**
     * Variável que armazena uma lista de objetos Device.
     */
    private ArrayList<Device> devices;
    /**
     * Variável que armazena uma lista de objetos Sensor.
     */
    private ArrayList<Sensor> sensors;
    private ArrayList<Actuator> actuators;
    /**
     * Variável que indica se o login foi realizado.
     */
    private boolean loginMade = false;

    private boolean usersRoomDone = false;
    private boolean roomRemove = FALSE;

    private int color_back_rooms=R.color.iconBackgoundRooms;

    //-------------------------------------- TTN ---------------------------------------------

    /**
     * Método estático sincronizado que retorna o hashmap string_send_ttn.
     * @return HashMap<Integer, String> - retorna o hashmap string_send_ttn.
     */
    public static synchronized HashMap<Integer, String> getString_send_ttn() {
        return HouseManager.string_send_ttn;
    }
    /**
     * Método estático que adiciona uma string ao hashmap string_send_ttn.
     * @param channel int - o canal a ser adicionado.
     * @param string_to_add String - a string a ser adicionada.
     */
    public static void addString_send_ttn(int channel, String string_to_add) {
        HouseManager.getString_send_ttn().put(channel, string_to_add);
    }
    /**
     * Variável estática que armazena o hashmap de strings a serem enviadas para o TTN.
     */
    private static HashMap<Integer, String> string_send_ttn = new HashMap<Integer, String>();

    /**
     * Variável estática que armazena as mensagens recebidas pelo TTN.
     */
    public static StringBuilder msgs_received = new StringBuilder();

    /**
     * Método que retorna as mensagens recebidas pelo TTN.
     * @return StringBuilder - retorna o objeto StringBuilder que armazena as mensagens recebidas.
     */
    public StringBuilder getMsgs_received() {
        return msgs_received;
    }


    // ------------------------------------- Sensors -------------------------------------

    /**
     * Método que adiciona um sensor a lista de sensores. Ele não adiciona o sensor se ele já existe na lista.
     * @param sensor Sensor - o sensor a ser adicionado.
     */
    public void addSensor(Sensor sensor) {
        if (sensors.isEmpty() || !sensors.contains(sensor)) {
            sensors.add(sensor);
            Collections.sort(sensors);
        }
    }

    /**
     * Método que remove um sensor da lista de sensores.
     * @param pos int - a posição do sensor na lista.
     */
    public void removeSensor(int pos) {
        sensors.remove(pos);
    }

    /**
     * Método que retorna um sensor de acordo com sua posição na lista.
     * @param pos int - a posição do sensor na lista.
     * @return Sensor - o sensor encontrado.
     */
    public Sensor getSensor(int pos) {
        return sensors.get(pos);
    }

    /**
     * Método que retorna a lista completa de sensores.
     * @return ArrayList<Sensor> - a lista de sensores.
     */
    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    /**
     * Método que procura sensores de acordo com um nome específico e retorna uma lista com os sensores encontrados.
     * @param name String - o nome do sensor a ser procurado.
     * @return ArrayList<Sensor> - a lista de sensores encontrados.
     */
    public ArrayList<Sensor> searchSensors(String name) {
        ArrayList<Sensor> result = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if ((sensor.getName().toUpperCase()).contains(name.toUpperCase()))
                result.add(sensor);
        }
        return result;
    }

    /**
     * Método que procura sensores de acordo com um tipo de dispositivo específico e retorna uma lista com os sensores encontrados.
     * @param deviceType DeviceType - o tipo de dispositivo do sensor a ser procurado.
     * @return ArrayList<Sensor> - a lista de sensores encontrados.
     */
    public ArrayList<Sensor> searchSensors(DeviceType deviceType) {
        ArrayList<Sensor> result = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (sensor.getType() == deviceType)
                result.add(sensor);
        }
        return result;
    }

    // ------------------------------------- Devices -------------------------------------

    /**
     * Método que adiciona um dispositivo a lista de dispositivos. Ele não adiciona o dispositivo se ele já existe na lista.
     * @param device Device - o dispositivo a ser adicionado.
     */
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

    public boolean isChannelAvailable(int channel, int deviceMode){
        if (deviceMode == 0){ // Sensor Mode
            for(Sensor sensor : sensors){
                if(sensor.getChannel() == channel){
                    return false;
                }
            }
        }else{ // Actuator Mode
            for (Actuator actuator : actuators){
                if(actuator.getChannel() == channel){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Método que remove um dispositivo da lista de dispositivos.
     * @param position int - a posição do dispositivo na lista.
     */
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
            ((Sensor) device).getAssociatedActuator().setAssociatedSensor(null);
            ((Sensor) device).getAssociatedActuator().setAssociateddSensorRef(null);
        }else{
            actuators.remove(device);
        }
    }
    /**
     * Método que retorna um dispositivo de acordo com sua posição na lista.
     * @param pos int - a posição do dispositivo na lista.
     * @return Device - o dispositivo encontrado.
     */
    public Device getDevice(int pos) {
        return devices.get(pos);
    }

    /**
     * Método que retorna a lista completa de dispositivos.
     * @return ArrayList<Device> - a lista de dispositivos.
     */
    public ArrayList<Device> getDevices() {
        return devices;
    }

    /**
     * Método que procura dispositivos de acordo com um nome específico e retorna uma lista com os dispositivos encontrados.
     * @param name String - o nome do dispositivo a ser procurado.
     * @return ArrayList<Device> - a lista de dispositivos encontrados.
     */
    public ArrayList<Device> searchDevices(String name) {
        ArrayList<Device> result = new ArrayList<>();
        for (Device device : devices) {
            if ((device.getName().toUpperCase()).contains(name.toUpperCase()))
                result.add(device);
        }
        return result;
    }

    /**
     * Método que procura um sensor específico através do canal.
     * @param channel int - o canal do sensor a ser procurado.
     * @return Sensor - o sensor encontrado.
     */
    public Sensor searchSensorByChannel(int channel) {
        for (Sensor sensor : sensors) {
            if(sensor.getChannel()==channel){
                return sensor;
            }
        }
        return null;
    }

    /**
     * Método que salva o estado prévio da conexão dos atuadores
     */
    public void saveActuatorValue(){
        for (Actuator actuator : actuators){
            actuator.setConnectionStateSaved(actuator.isConnectionState());
        }
    }

    /**
     * Método que salva o estado atual da conexão dos atuadores
     */
    public void recoverSavedActuatorValue(){
        for (Actuator actuator : actuators){
            actuator.setConnectionState(actuator.isConnectionStateSaved());
        }
    }

    /**
     * Método que retorna o número de dispositivos conectados.
     * @return int - o número de dispositivos conectados.
     */
    public int numberOfDevicesConnect(ArrayList<Device> devicesList){
        int devicesConnected = 0;
        for (Device device : devicesList){
            if (device.isConnectionState()){
                devicesConnected ++;
            }
        }
        return devicesConnected;
    }

    /**
     * Método que muda o estado da conexão dos devices
     * @param connectionState estado a mudar
     */
    public void changeDevicesConnectionState(boolean connectionState){
        if (devices != null){
            for (Device device : devices){
                device.setConnectionState(connectionState);
            }
        }
    }
    /**
     *Método que adiciona dispositivos iniciais ao sistema.
     * Este método cria novos objetos de dispositivos e os adiciona ao sistema, incluindo sensores e atuadores.
     * Ele também adiciona notificações a um dispositivo específico e gera dados de exemplo para os dispositivos.
     */
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

    /**
     * Método que retorna se o quarto foi removido
     * @return true se removido, false se não
     */
    public boolean isRoomRemove() {
        return roomRemove;
    }

    /**
     * Método que define a flag que diz se o quarto foi removido ou não
     * @param roomRemove flag a dizer se foi removido ou não
     */
    public void setRoomRemove(boolean roomRemove) {
        this.roomRemove = roomRemove;
    }

    /**
     * Adiciona um quarto à lista de quartos existentes, coloca num mapa para escrever esse mesmo mapa na firebase
     *
     * @param room : quarto a ser adicionado
     */
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

    /**
     * Remove um quarto da lista de quartos existentes
     * @param room : quarto a ser removido
     */
    public void removeRoom(Room room) {
        if (!rooms.contains(room)) {
            return;
        }

        HouseManager.getInstance().getUser().getRoomsRef().document(room.getRoom_Name()).delete();
        rooms.remove(room);
        Collections.sort(rooms);
    }
    /**
     * Método que adiciona dados iniciais à lista de salas.
     */
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

    /**
     * Método que retorna a sala em uma determinada posição na lista de salas.
     * @param pos - A posição da sala na lista.
     * @return A sala na posição especificada.
     */
    public Room getRoom(int pos) {
        return rooms.get(pos);
    }

    /**
     * Método que retorna a lista de salas.
     * @return A lista de salas.
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    /**
     * Método que retorna o indice da sala na lista de salas.
     * @param room - A sala procurada.
     * @return o indice da sala na lista de salas.
     */
    public int getRoomIndex(Room room){
        return rooms.indexOf(room);
    }

    /**
     * Método setRooms() é utilizado para definir uma lista de quartos para a instância atual da classe HouseManager.
     * @param rooms Lista de quartos a serem adicionados a instância atual da classe HouseManager.
     */
    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Método getUserRooms() é utilizado para obter os quartos associados ao usuário atual, através da conexão com o banco de dados.
     * Este método também limpa a lista de dispositivos e sensores existente na instância atual da classe HouseManager.
     * @param adapter Adapter de quartos que será atualizado com a lista de quartos obtida.
     * @param loadingDialog Diálogo de carregamento que será fechado após a conclusão da obtenção dos quartos.
     */
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

    /**
     *
     * Método que busca uma sala pelo dispositivo especificado
     * @param device O dispositivo para buscar a sala correspondente.
     * @return A sala correspondente ao dispositivo especificado.
     */
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




    /**
     * Método que retorna a cor de fundo das salas.
     * @return A cor de fundo das salas.
     */
    public int getColor_back_rooms() {
        return color_back_rooms;
    }

    /**
     * Método que define a cor de fundo das salas.
     * @param color_back_rooms A nova cor de fundo das salas.
     */
    public void setColor_back_rooms(int color_back_rooms) {
        this.color_back_rooms = color_back_rooms;
    }

    /**
     * Método que retorna se a busca pelas salas do usuário foi concluída ou não.
     * @return Verdadeiro se a busca foi concluída, falso caso contrário
     */
    public boolean isUsersRoomDone() {
        return usersRoomDone;
    }

    /**
     * Método que define se a busca pelas salas do usuário foi concluida
     * @param usersRoomDone true se concluida, false se não
     */
    public void setUsersRoomDone(boolean usersRoomDone) {
        this.usersRoomDone = usersRoomDone;
    }

    // ------------------------------------- House Manager -------------------------------------
    //Código adicionado para garantir que há só uma instância da classe HouseManager

    /**
     * Método getInstance, responsável por garantir uma instância única da classe.
     * @return instância única da classe HouseManager.
     */
    public static synchronized HouseManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HouseManager();
        }
        return INSTANCE;
    }

    /**
     * Construtor privado da classe HouseManager, inicializando os atributos.
     */
    private HouseManager() {
        rooms = new ArrayList<>();
        devices = new ArrayList<>();
        sensors = new ArrayList<>();
        actuators = new ArrayList<>();
        bundle = new Bundle();
    }

    /**
     * Método getGatewayBLEServerName, responsável por retornar o nome do servidor BLE gateway.
     * @return o nome do servidor BLE gateway.
     */
    public String getGatewayBLEServerName() {
        return GatewayBLEServerName;
    }

    /**
     * Método responsável por setar o nome do gateway BLE Server.
     * @param gatewayBLEServerName nome do gateway BLE Server.
     */
    public void setGatewayBLEServerName(String gatewayBLEServerName) {
        GatewayBLEServerName = gatewayBLEServerName;
    }

    /**
     * Método responsável por retornar o código DevEui do gateway BLE Server.
     * @return código DevEui do gateway BLE Server.
     */
    public String getGatewayBLEServerDevEuiCode() {
        return GatewayBLEServerDevEuiCode;
    }

    /**
     * Método responsável por setar o código DevEui do gateway BLE Server.
     * @param gatewayBLEServerDevEuiCode código DevEui do gateway BLE Server.
     */
    public void setGatewayBLEServerDevEuiCode(String gatewayBLEServerDevEuiCode) {
        GatewayBLEServerDevEuiCode = gatewayBLEServerDevEuiCode;
    }

    /**
     * Método responsável por retornar o objeto Bundle.
     * @return objeto Bundle.
     */
    public static synchronized Bundle getBundle() {
        return bundle;
    }

    /**
     * Método responsável por setar o objeto Bundle.
     * @param bundle objeto Bundle.
     */
    public static synchronized void setBundle(Bundle bundle) {
        HouseManager.bundle = bundle;
    }

    public static boolean isModificable() {
        return modificable;
    }
    /**
     * Método toString da classe HouseManager, retorna uma string com as informações de todas as salas adicionadas.
     * @return String com as informações de todas as salas adicionadas.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rooms.size(); i++) {
            str.append(rooms.get(i)).append("\n");
        }
        return str.toString();
    }

    /**
     * Método para gravar as informações da classe HouseManager em um arquivo binário no dispositivo.
     * @param context Contexto da aplicação.
     */
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

    /**
     * Método que lê o ficheiro houseManager.bin e atualiza a instância de HouseManager.
     * @param context Contexto da aplicação
     */
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

    /**
     * Método que obtém o utilizador atualmente associado a HouseManager.
     * @return O objeto User atualmente associado a HouseManager
     */
    public User getUser() {
        return user;
    }

    /**
     * Método que atribui um utilizador a HouseManager.
     * @param user O objeto User a ser associado a HouseManager
     */
    public void setUser(User user) {
        HouseManager.userRoomsRefGotten = false;
        this.user = user;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users").document(user.getEmail()).collection("rooms");

        HouseManager.getInstance().getUser().setRoomsRef(usersRef);
        HouseManager.userRoomsRefGotten=true;

    }

    /**
     * Método que define o utilizador atual
     * @param currentUser utilizador atual
     */
    public void setCurrentUser(FirebaseUser currentUser) {
        String user_mail = currentUser.getEmail();
        User user = new User(user_mail);
        setUser(user);
    }

    //---------------------LOGIN-------------------------

    /**
     * Método que verifica se o login foi feito
     * @return true se o login foi feito, false caso contrário
     */
    public boolean isLoginMade() {
        return loginMade;
    }

    /**
     * Método que define se o login foi feito
     * @param loginMade boolean indicando se o login foi feito
     */
    public void setLoginMade(boolean loginMade) {
        this.loginMade = loginMade;
    }


    //-------------------------------------------------
}
