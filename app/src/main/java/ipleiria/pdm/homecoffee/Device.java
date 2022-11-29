package ipleiria.pdm.homecoffee;

import java.io.Serializable;

import ipleiria.pdm.homecoffee.Enums.DeviceType;

public class Device implements Serializable, Comparable<Device> {
    private int numero;
    private String name;
    private boolean connectionState;
    private boolean connectionStateSaved;
    private DeviceType type;
    private Room room;

    public Device(int numero, String nome, DeviceType type, Room room) {
        this.numero = numero;
        this.name = nome;
        this.type = type;
        this.room = room;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
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

    @Override
    public boolean equals(Object obj) {
        return this.numero == ((Device) obj).numero;
    }

    @Override
    public String toString() {
        return name + " - " + numero;
    }

    @Override
    public int compareTo(Device device) {
        return 0;
    }
}