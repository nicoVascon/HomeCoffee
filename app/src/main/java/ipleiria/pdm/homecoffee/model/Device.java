package ipleiria.pdm.homecoffee.model;

import com.jjoe64.graphview.series.DataPoint;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

public abstract class Device implements Serializable, Comparable<Device> {
    protected int channel;
    protected String name;
    protected boolean connectionState;
    protected boolean connectionStateSaved;
    protected DeviceType type;
    protected ArrayList<DataPointImpl> dataPoints;
    protected ArrayList<Notification> notifications;
    protected double value;
    protected double valueSaved;

    public Device(){
    }


    public Device(int channel, String name, DeviceType type, Room room) {
        this.channel = channel;
        this.name = name;
        this.type = type;

        this.notifications = new ArrayList<>();
        this.dataPoints = new ArrayList<>();
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConnectionState() {
        return connectionState;
    }

    public void setConnectionState(boolean connectionState) {
        this.connectionState = connectionState;
    }

    public boolean isConnectionStateSaved() {
        return connectionStateSaved;
    }

    public void setConnectionStateSaved(boolean connectionStateSaved) {
        this.connectionStateSaved = connectionStateSaved;
    }

    public double getValueSaved() {
        return valueSaved;
    }

    public void setValueSaved(double valueSaved) {
        this.valueSaved = valueSaved;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getNumNotifications(){
        return notifications.size();
    }

    public Notification getNotification(int position){
        return notifications.get(position);
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(Notification newNotification){
        if (newNotification != null){
            notifications.add(newNotification);
        }
    }

    public void removeNotification(int position){
        notifications.remove(position);
    }

    public void set_Room(Room room) {
        room.addDevice(this);
        room.updateRoomDev();
    }

    public ArrayList<DataPointImpl> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(ArrayList<DataPointImpl> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public void addDataPoint(DataPointImpl dataPoint){
        if(dataPoint != null){
            this.dataPoints.add(dataPoint);
            try {
                Collections.sort(dataPoints);
            }catch (NullPointerException e){
                System.out.println("Exception addDataPoint: " + e.getMessage());
                for(int i = 0; i < this.dataPoints.size(); i++){
                    if(this.dataPoints.get(i) == null){
                        this.dataPoints.remove(i);
                    }
                }
                Collections.sort(dataPoints);
            }

        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass()) && this.channel == ((Device) obj).channel;
    }

    @Override
    public String toString() {
        return name + " - " + channel;
    }

    @Override
    public int compareTo(Device device) {
        return 0;
    }
}