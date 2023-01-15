package ipleiria.pdm.homecoffee.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

public class Actuator extends Device{

    private Sensor associatedSensor;
    private DocumentReference associateddSensorRef;

    public Actuator(){
        this.notifications = new ArrayList<>();
        this.dataPoints = new ArrayList<>();
    }

    public Actuator(int channel, String name, DeviceType type, Room room) {
        super(channel, name, type, room);
    }

    public Sensor setAssociatedSensor(@Nullable Sensor sensor){
        if (sensor == null){
            associatedSensor = null;
        }
        if(sensor.getType() == this.type){
            associatedSensor = sensor;
            this.dataPoints = sensor.dataPoints;
            this.notifications = sensor.notifications;
            associatedSensor.setAssociatedActuator(this);

            CollectionReference refRooms = HouseManager.getInstance().getUser().getRoomsRef();
            DocumentReference refRealRoom= refRooms.document(sensor.getRoom().getRoom_Name());
            CollectionReference refSensors = refRealRoom.collection("Sensors");
            DocumentReference refSensor = refSensors.document(String.valueOf(sensor.getChannel()));
            setAssociateddSensorRef(refSensor);
        }
        return associatedSensor;
    }

    public Sensor getAssociatedSensor() {
        return associatedSensor;
    }

    public double MeasuredValue(){
        return this.associatedSensor != null ? this.associatedSensor.getValue() : 0.0;
    }

    public void setDesiredValue(double value){
        if(sendValueChangeCommand(value)){
            this.value = value;
            if(associatedSensor == null){
                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();
                this.dataPoints.add(new DataPointImpl(currentDate, value));
            }
        }
    }

    public DocumentReference getAssociateddSensorRef() {
        return associateddSensorRef;
    }

    public void setAssociateddSensorRef(DocumentReference associateddSensorRef) {
        this.associateddSensorRef = associateddSensorRef;
    }

    @Override
    public void setConnectionState(boolean connectionState) {
        super.setConnectionState(connectionState);
        if (!connectionState){
            this.setValueSaved(this.getValue());
            this.setDesiredValue(0.0);
        }else{
            this.setDesiredValue(this.getValueSaved());
        }
    }

    private boolean sendValueChangeCommand(double newValue){
        // Code for App-ESP32 communication
        if(this.type == DeviceType.DIGITAL || this.type == DeviceType.LUMINOSITY){
            HouseManager.addString_send_ttn(this.channel, this.channel + "," + ((int) newValue) );
        }else{
            HouseManager.addString_send_ttn(this.channel, this.channel + "," + newValue );
        }
        return true;
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
        device.put("associateddSensorRef", this.getAssociateddSensorRef());
        HouseManager.getInstance().getUser().getRoomsRef().
                document(this.room.getRoom_Name()).collection("Sensors").
                document(String.valueOf(this.getChannel())).set(device);
    }
}
