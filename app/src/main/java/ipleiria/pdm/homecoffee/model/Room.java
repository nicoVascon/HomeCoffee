package ipleiria.pdm.homecoffee.model;

import java.io.Serializable;
import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Enums.RoomType;

/**
 * Classe que representa uma sala. Possui um nome, tipo de sala e uma lista de dispositivos associados.
 *
 * Implementa a interface Serializable para permitir que sua instância seja salva em arquivo e comparável.
 */
public class Room implements Serializable, Comparable<Room> {

    /**
     * Nome da sala.
     */
    private String nome;
    /**
     * Tipo de sala.
     */
    private RoomType room_type;
    /**
     * Lista de dispositivos associados à sala.
     */
    private ArrayList<Device> devices;

    /**
     * ria uma instância de sala com nome e tipo especificados.
     *
     * @param nome Nome da sala.
     * @param room_type Tipo de sala.
     */
    public Room( String nome, RoomType room_type) {

        this.nome = nome;
        //this.pathPhoto = pathPhoto;
        this.room_type=room_type;
        this.devices=new ArrayList<>();
    }

    /**
     * Obtém o nome da sala.
     * @return Nome da sala.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Adiciona um dispositivo à sala.
     * @param dev Dispositivo a ser adicionado.
     */
    public void addDevice(Device dev){
        devices.add(dev);
    }

    /**
     * Obtém a lista de dispositivos associados à sala.
     * @return Lista de dispositivos associados à sala.
     */
    public ArrayList<Device> getDevices(){
        return devices;
    }

    /**
     * Retorna o tipo de sala.
     * @return room_type tipo de sala
     */
    public RoomType getType() {
        return room_type;
    }

    /**
     * Define o tipo de sala.
     * @param type tipo de sala
     */
    public void setType(RoomType type) {
        this.room_type = type;
    }

    /**
     * Compara objetos Room.
     * @param obj objeto a ser comparado
     * @return true se nomes de sala forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        return this.nome == ((Room) obj).nome;
    }

    /**
     * Retorna a representação string do objeto Room.
     * @return string representando nome e tipo de sala.
     */
    @Override
    public String toString() {
        return nome + " - " + room_type;
    }

    /**
     * Compara objetos Room.
     * @param device objeto a ser comparado
     * @return 0, pois não há critério de ordenação estabelecido para o objeto Room.
     */
    @Override
    public int compareTo(Room device) {
        return 0;
    }
}