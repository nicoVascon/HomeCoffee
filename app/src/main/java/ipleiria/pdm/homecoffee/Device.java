package ipleiria.pdm.homecoffee;

import java.io.Serializable;

public class Device implements Serializable, Comparable<Device> {
    private int numero;
    private String name;
    private boolean connectionState;
    private DeviceType type;
    private String pathPhoto;

    public Device(int numero, String nome, DeviceType type, String pathPhoto) {
        this.numero = numero;
        this.name = nome;
        this.type = type;
        this.pathPhoto = pathPhoto;
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

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getPathPhoto() {
        return pathPhoto;
    }

    public void setPathPhoto(String pathPhoto) {
        this.pathPhoto = pathPhoto;
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