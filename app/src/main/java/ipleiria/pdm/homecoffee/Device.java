package ipleiria.pdm.homecoffee;

import java.io.Serializable;

public class Device implements Serializable, Comparable<Device> {
    private int numero;
    private String nome;
    private String pathPhoto;

    public Device(int numero, String nome, String pathPhoto) {
        this.numero = numero;
        this.nome = nome;
        this.pathPhoto = pathPhoto;
    }
    public int getNumero() {
        return numero;
    }
    public String getNome() {
        return nome;
    }
    public String getPathPhoto() {
        return pathPhoto;
    }
    @Override
    public boolean equals(Object obj) {
        return this.numero == ((Device) obj).numero;
    }
    @Override
    public String toString() {
        return nome + " - " + numero;
    }


    @Override
    public int compareTo(Device device) {
        return 0;
    }
}