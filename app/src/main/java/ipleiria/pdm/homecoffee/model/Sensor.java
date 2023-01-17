package ipleiria.pdm.homecoffee.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

/**
 * Classe Sensor, que representa um sensor.
 * Ela é uma extensão da classe Device
 */
public class Sensor extends Device{
    /**
     * Referencia no firebase do documento do quarto associado
     */
    private DocumentReference associatedRoomRef;
    /**
     * Atuador associado a este sensor
     */
    private ArrayList<Actuator> associatedActuatorsList;

    /**
     * Construtor vazio que apenas inicia os arrays usado no processo de reconstruir os dados que recupera da firebase
     */
    public Sensor(){
        this.notifications = new ArrayList<>();
        this.dataPoints = new ArrayList<>();
        this.associatedActuatorsList = new ArrayList<>();
    }
    /**
     * Construtor da classe Sensor
     * @param channel canal do dispositivo
     * @param name nome do dispositivo
     * @param type tipo do dispositivo
     * @param room quarto onde o dispositivo se encontra
     */
    public Sensor(int channel, String name, DeviceType type, Room room) {
        super(channel, name, type, room);
        this.associatedActuatorsList = new ArrayList<>();
    }

    /**
     * Método que atualiza o valor do sensor
     * @return retorna verdadeiro se o valor foi atualizado com sucesso
     */
    public boolean updateValue(){
        // Code for receive values from database
        double receivedValue = 25;
        this.value = receivedValue;
        //
        return true;
    }

    // Temporal method
    /**
     * Método temporal para definir o valor do sensor
     * @param value valor a ser definido
     */
    public void setValue(double value){
        super.setValue(value);
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        this.addDataPoint(new DataPointImpl(currentDate, value));
        this.addNotification(new Notification(currentDate, "Novo Valor Recebido: " + String.format("%.2f", value)));
    }

    public DocumentReference getAssociatedRoomRef() {
        return associatedRoomRef;
    }

    public ArrayList<Actuator> getAssociatedActuatorsList() {
        return associatedActuatorsList;
    }

    public void addAssociatedActuator(Actuator associatedActuator) {
        if(associatedActuatorsList != null && !associatedActuatorsList.contains(associatedActuator)){
            this.associatedActuatorsList.add(associatedActuator);
        }
    }

    public void setAssociatedRoomRef(DocumentReference associatedRoomRef) {
        this.associatedRoomRef = associatedRoomRef;
    }

    @Override
    public void update() {
        Map<String, Object> device = new HashMap<>();
        device.put("channel", this.getChannel());
        device.put("name", this.getName());
        device.put("type", this.getType());
        device.put("connectionState", this.isConnectionState());
        device.put("connectionStateSaved", this.isConnectionStateSaved());
        device.put("dataPoints", this.getDataPoints());
        device.put("notifications", this.getNotifications());
        device.put("value", this.getValue());
        device.put("valueSaved", this.getValueSaved());
        DocumentReference roomRef = HouseManager.getInstance().getUser().getRoomsRef().document(this.room.getRoom_Name());
        device.put("associatedRoomRef",roomRef);
        HouseManager.getInstance().getUser().getRoomsRef().
                document(this.room.getRoom_Name()).collection("Sensors").
                document(String.valueOf(this.getChannel())).set(device);
    }
}
