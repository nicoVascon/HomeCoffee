package ipleiria.pdm.homecoffee.model;

import com.google.firebase.firestore.DocumentReference;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

public class Sensor extends Device{

    private DocumentReference associatedRoomRef;

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
        this.value = value;
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        this.addDataPoint(new DataPointImpl(currentDate, value));
        this.addNotification(new Notification(currentDate, "Novo Valor Recebido: " + String.format("%.2f", value)));
    }

    public DocumentReference getAssociatedRoomRef() {
        return associatedRoomRef;
    }

    public void setAssociatedRoomRef(DocumentReference associatedRoomRef) {
        this.associatedRoomRef = associatedRoomRef;
    }
}
