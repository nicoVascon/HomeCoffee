package ipleiria.pdm.homecoffee.model;

import com.google.firebase.firestore.DocumentReference;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

public class Sensor extends Device{

    private DocumentReference associatedRoomRef;
    private Actuator associatedActuator;

    public Sensor(){
        this.notifications = new ArrayList<>();
        this.dataPoints = new ArrayList<>();
    }

    public Sensor(int channel, String name, DeviceType type, Room room) {
        super(channel, name, type, room);
    }

    public boolean updateValue(){
        // Code for receive values from database
        double receivedValue = 25;
        this.value = receivedValue;
        //
        return true;
    }

    // Temporal method
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

    public Actuator getAssociatedActuator() {
        return associatedActuator;
    }

    public void setAssociatedActuator(Actuator associatedActuator) {
        this.associatedActuator = associatedActuator;
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
