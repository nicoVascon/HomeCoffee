package ipleiria.pdm.homecoffee.model;

import java.io.Serializable;
import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Enums.RoomType;

public class Room implements Serializable, Comparable<Room> {

    private String nome;
    //private String pathPhoto;
    private RoomType room_type;
    private ArrayList<Device> devices;


    public Room( String nome, RoomType room_type) {

        this.nome = nome;
        //this.pathPhoto = pathPhoto;
        this.room_type=room_type;
        this.devices=new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void addDevice(Device dev){
        devices.add(dev);
    }
    public ArrayList<Device> getDevices(){
        return devices;
    }

    public RoomType getType() {
        return room_type;
    }

    public void setType(RoomType type) {
        this.room_type = type;
    }

    @Override
    public boolean equals(Object obj) {
        return this.nome == ((Room) obj).nome;
    }

    @Override
    public String toString() {
        return nome + " - " + room_type;
    }

    @Override
    public int compareTo(Room device) {
        return 0;
    }
}