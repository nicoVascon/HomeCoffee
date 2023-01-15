package ipleiria.pdm.homecoffee.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ipleiria.pdm.homecoffee.Enums.RoomType;
import ipleiria.pdm.homecoffee.HouseManager;

/**
 * Classe que representa uma sala. Possui um nome, tipo de sala e uma lista de dispositivos associados.
 *
 * Implementa a interface Serializable para permitir que sua instância seja salva em arquivo e comparável.
 */
public class Room implements Serializable, Comparable<Room> {
    /**
     * Nome da sala.
     */
    private String Room_Name;
    /**
     * Tipo de sala.
     */
    private RoomType Room_Type;
    /**
     * Lista de dispositivos sensores associados à sala.
     */
    private ArrayList<Sensor> Sensors;
    /**
     * Lista de dispositivos atuadores associados à sala.
     */
    private ArrayList<Actuator> Actuators;

    public Room() {
        // Default constructor required for calls to DataSnapshot.toObject(Room.class)
        this.Sensors = new ArrayList<>();
        this.Actuators = new ArrayList<>();
    }
    /**
     * Cria uma instância de sala com nome e tipo especificados.
     *
     * @param Room_Name Nome da sala.
     * @param Room_Type Tipo de sala.
     */
    public Room( String Room_Name, RoomType Room_Type) {

        this.Room_Name = Room_Name;
        //this.pathPhoto = pathPhoto;
        this.Room_Type =Room_Type;
//        this.Devices =new ArrayList<>();
        this.Sensors = new ArrayList<>();
        this.Actuators = new ArrayList<>();
    }

//    public Room( String Room_Name, RoomType Room_Type,ArrayList<Device> devices) {
//
//        this.Room_Name = Room_Name;
//        //this.pathPhoto = pathPhoto;
//        this.Room_Type =Room_Type;
//        this.Devices =devices;
//    }

    /**
     *  Construtor da classe Room
     * @param Room_Name Nome do quarto.
     * @param Room_Type tipo do quarto.
     * @param sensors lista de sensores presentes no quarto.
     * @param actuators lista de atuadores presentes no quarto.
     */
    public Room( String Room_Name, RoomType Room_Type,ArrayList<Sensor> sensors, ArrayList<Actuator> actuators) {

        this.Room_Name = Room_Name;
        //this.pathPhoto = pathPhoto;
        this.Room_Type =Room_Type;
        this.Sensors =sensors;
        this.Actuators = actuators;
    }
    /**
     * Obtém o nome da sala.
     * @return Nome da sala.
     */
    public String getRoom_Name() {
        return Room_Name;
    }


    /**
     * Método que atualiza um dispositivo Sensor específico na coleção "Sensors" do quarto correspondente no banco de dados.
     * @param sensor o objeto Sensor que será atualizado
     */
    public void updateRoomDevice(Sensor sensor){
        HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).collection("Sensors").document(String.valueOf(sensor.getChannel())).set(sensor);

    }

    /**
     * Método que atualiza todos os dispositivos do tipo Sensor contidos na lista de Sensors deste quarto no banco de dados.
     */
    public void updateRoomDev(){


//
//
//        for (Sensor sensor : Sensors) {
//
//            HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).collection("Sensors").document(String.valueOf(sensor.getChannel())).set(sensor);
//        }

    }

    /**
     * Método para remover um dispositivo específico do quarto, tanto de sua lista interna quanto do banco de dados.
     * @param device o objeto Device a ser removido
     */
    public void removeDevice(Device device){
        if(device instanceof Sensor){
            if(!Sensors.contains(device)){
                return;
            }
            DocumentReference roomRef = HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name);
            roomRef.collection("Sensors").document(String.valueOf(device.getChannel())).delete();
            Sensors.remove((Sensor) device);
        }else {
            if(!Actuators.contains(device)){
                return;
            }
            HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).
                    collection("Actuators").document(String.valueOf(device.getChannel())).delete();
            Actuators.remove((Actuator) device);
        }
    }

    /**
     * Adiciona um dispositivo à sala.
     * @param dev Dispositivo a ser adicionado.
     */
    public void addDevice(Device dev){

        dev.set_Room(this);
        Map<String, Object> device = new HashMap<>();
        //room.put("User_Email", userMail);
        device.put("channel", dev.getChannel());
        device.put("name", dev.getName());
        device.put("type", dev.getType());
        device.put("connectionState", dev.isConnectionState());
        device.put("connectionStateSaved", dev.isConnectionStateSaved());
        device.put("dataPoints", dev.getDataPoints());
        device.put("notifications", dev.getNotifications());
        device.put("value", dev.getValue());
        device.put("valueSaved", dev.getValueSaved());


//        protected boolean connectionState;
//        protected boolean connectionStateSaved;
//        protected DeviceType type;
//        protected ArrayList<DataPointImpl> dataPoints;
//        protected ArrayList<Notification> notifications;
//        protected double value;
//        protected double valueSaved;
//        private DocumentReference docRefFirebase;

        if(dev instanceof Sensor){
            if(Sensors.contains(dev)){
                return;
            }
            Sensors.add((Sensor) dev);
            DocumentReference roomRef = HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name);
            device.put("associatedRoomRef",roomRef);
            roomRef.collection("Sensors").document(String.valueOf(dev.getChannel())).set(device);

        }else {
            if(Actuators.contains(dev)){
                return;
            }
            Actuators.add((Actuator) dev);

            device.put("associateddSensorRef", ((Actuator) dev).getAssociateddSensorRef());


            HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).collection("Actuators").document(String.valueOf(dev.getChannel())).set(device);

        }
    }
    /**
     * Obtém a lista de dispositivos associados à sala.
     * @return Lista de dispositivos associados à sala.
     */
    public ArrayList<Device> getDevices(){
        ArrayList<Device> result = new ArrayList<>(Sensors.size() + Actuators.size());
        result.addAll(Sensors);
        result.addAll(Actuators);
        return result;
    }
    /**
     * Retorna o tipo de sala.
     * @return room_type tipo de sala
     */
    public RoomType getRoom_Type() {
        return Room_Type;
    }
    /**
     * Define o tipo de sala.
     * @param type tipo de sala
     */
    public void setRoom_Type(RoomType type) {
        this.Room_Type = type;
    }

    public ArrayList<Sensor> getSensors() {
        return Sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.Sensors = sensors;
    }

    public ArrayList<Actuator> getActuators() {
        return Actuators;
    }

    public void setActuators(ArrayList<Actuator> actuators) {
        this.Actuators = actuators;
    }
    /**
     * Compara objetos Room.
     * @param obj objeto a ser comparado
     * @return true se nomes de sala forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        return this.Room_Name == ((Room) obj).Room_Name;
    }
    /**
     * Retorna a representação string do objeto Room.
     * @return string representando nome e tipo de sala.
     */
    @Override
    public String toString() {
        return Room_Name + " - " + Room_Type;
    }
    /**
     * Compara objetos Room.
     * @param room objeto a ser comparado
     * @return 0, pois não há critério de ordenação estabelecido para o objeto Room.
     */
    @Override
    public int compareTo(Room room) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.Room_Name, room.Room_Name);
    }

    /**
     * Método que adiciona um device ao room no array local
     * @param dev device a adicionar
     */
    public void addLocalDevice(Device dev) {

        if(dev instanceof Sensor){
            Sensors.add((Sensor) dev);
        }else{
            Actuators.add((Actuator) dev);
        }
        dev.set_Room(this);
    }
}