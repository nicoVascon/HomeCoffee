package ipleiria.pdm.homecoffee.model;

import ipleiria.pdm.homecoffee.Enums.DeviceType;

public class Sensor extends Device{
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
        this.value = value;
    }
}