package ipleiria.pdm.homecoffee.model;

import java.io.Serializable;

import ipleiria.pdm.homecoffee.Enums.DeviceType;

public class Device implements Serializable, Comparable<Device> {
    private int channel;
    private String name;
    private boolean connectionState;
    private boolean connectionStateSaved;
    private DeviceType type;
    private Room room;
    private double value;

    public Device(int channel, String name, DeviceType type, Room room) {
        this.channel = channel;
        this.name = name;
        this.type = type;
        this.room = room;
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

    @Override
    public boolean equals(Object obj) {
        return this.channel == ((Device) obj).channel;
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