package ipleiria.pdm.homecoffee;

import java.io.Serializable;

public class Room implements Serializable, Comparable<Room> {
    private Device device;
    private String nome;
    private String pathPhoto;

    public Room(int numero, String nome, String pathPhoto) {

        this.nome = nome;
        this.pathPhoto = pathPhoto;
    }

    public String getNome() {
        return nome;
    }
    public String getPathPhoto() {
        return pathPhoto;
    }
    //@Override
    //public boolean equals(Object obj) {
    //   return this.numero == ((Room) obj).numero;
    //}
    @Override
    public String toString() {
        return nome + " - "     ;
    }


    @Override
    public int compareTo(Room room) {
        return 0;
    }
}