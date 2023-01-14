package ipleiria.pdm.homecoffee.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

//            DocumentReference referenciaSensor = HouseManager.getInstance().getUser().getRoomsRef().
//                    document(sensor.getRoom().getRoom_Name()).collection("Sensors").
//                    document(String.valueOf(sensor.getChannel()));
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

    public double getMeasuredValue(){
        return this.associatedSensor.getValue();
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

    private boolean sendValueChangeCommand(double newValue){
        // Code for App-ESP32 communication
        if(this.type == DeviceType.DIGITAL || this.type == DeviceType.LUMINOSITY){
            HouseManager.addString_send_ttn(this.channel, this.channel + "," + ((int) newValue) );
        }else{
            HouseManager.addString_send_ttn(this.channel, this.channel + "," + newValue );
        }

        return true;
    }


}
