package ipleiria.pdm.homecoffee.model;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;
import java.util.Date;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

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
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        this.addDataPoint(new DataPointImpl(currentDate, value));
        this.addNotification(new Notification(currentDate, "Novo Valor Recebido: " + String.format("%.2f", value)));
    }


}
