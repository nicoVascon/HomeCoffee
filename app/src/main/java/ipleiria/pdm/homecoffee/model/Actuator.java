package ipleiria.pdm.homecoffee.model;

import javax.annotation.Nullable;

import ipleiria.pdm.homecoffee.Enums.DeviceType;

public class Actuator extends Device{

    private Sensor associatedSensor;

    public Actuator(int channel, String name, DeviceType type, Room room) {
        super(channel, name, type, room);
    }

    public Sensor setAssociatedSensor(@Nullable Sensor sensor){
        if (sensor == null){
            associatedSensor = null;
        }
        if(sensor.getType() == this.type){
            associatedSensor = sensor;
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
        }
    }

    private boolean sendValueChangeCommand(double newValue){
        // Code for App-ESP32 communication
        return true;
    }
}
