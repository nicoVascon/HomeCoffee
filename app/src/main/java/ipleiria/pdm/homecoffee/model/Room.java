package ipleiria.pdm.homecoffee.model;

import java.io.Serializable;

import ipleiria.pdm.homecoffee.Enums.RoomType;

public class Room implements Serializable, Comparable<Room> {

    private String nome;
    //private String pathPhoto;
    private RoomType room_type;

    public Room( String nome, RoomType room_type) {

        this.nome = nome;
        //this.pathPhoto = pathPhoto;
        this.room_type=room_type;
    }

    public String getNome() {
        return nome;
    }
    /*public String getPathPhoto() {
        return pathPhoto;
    }*/

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