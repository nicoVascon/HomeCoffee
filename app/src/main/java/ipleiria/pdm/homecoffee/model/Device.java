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

/**
 * Classe abstrata que representa um dispositivo. Implementa a interface Serializable e Comparable<Device>.
 */
public abstract class Device implements Serializable, Comparable<Device> {
    /**
     * O canal do dispositivo
     */
    protected int channel;
    /**
     * O nome do dispositivo
     */
    protected String name;
    /**
     * Indica o estado da conexão do dispositivo
     */
    protected boolean connectionState;
    /**
     * Indica o estado da conexão salvo do dispositivo
     */
    protected boolean connectionStateSaved;
    /**
     * O tipo de dispositivo
     */
    protected DeviceType type;
    /**
     * O quarto onde o dispositivo está localizado
     */
    protected Room room;
    /**
     * A lista de pontos de dados do dispositivo
     */
    protected ArrayList<DataPointImpl> dataPoints;
    /**
     * A lista de notificações do dispositivo
     */
    protected ArrayList<Notification> notifications;
    /**
     * O valor medido pelo dispositivo
     */
    protected double value;
    /**
     * O valor salvo pelo dispositivo
     */
    protected double valueSaved;

    /**
     * Construtor da classe Device.
     *
     * @param channel o canal do dispositivo
     * @param name o nome do dispositivo
     * @param type o tipo de dispositivo
     * @param room o quarto onde o dispositivo está localizado
     */
    public Device(int channel, String name, DeviceType type, Room room) {
        this.channel = channel;
        this.name = name;
        this.type = type;
        this.room = room;
        this.notifications = new ArrayList<>();
        this.dataPoints = new ArrayList<>();

        room.addDevice(this);
    }

    /**
     * Retorna o canal do dispositivo
     * @return o canal
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Método setChannel: Define o canal da comunicação do dispositivo.
     * @param channel canal da comunicação do dispositivo
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * Método getName: Retorna o nome do dispositivo
     * @return o nome do dispositivo
     */
    public String getName() {
        return name;
    }

    /**
     * Método setName: Define o nome do dispositivo
     * @param name nome do dispositivo
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método isConnectionState: Verifica se o dispositivo está conectado
     * @return verdadeiro se o dispositivo estiver conectado, falso caso contrário
     */
    public boolean isConnectionState() {
        return connectionState;
    }

    /**
     * Método setConnectionState: Define o estado de conexão do dispositivo.
     * @param connectionState estado de conexão do dispositivo (verdadeiro se conectado, falso caso contrário)
     */
    public void setConnectionState(boolean connectionState) {
        this.connectionState = connectionState;
    }

    /**
     * Retorna o estado da conexão salvo.
     * @return estado da conexão salvo.
     */
    public boolean isConnectionStateSaved() {
        return connectionStateSaved;
    }

    /**
     * Define o estado da conexão salvo.
     * @param connectionStateSaved novo estado da conexão.
     */
    public void setConnectionStateSaved(boolean connectionStateSaved) {
        this.connectionStateSaved = connectionStateSaved;
    }

    /**
     * Retorna o valor salvo.
     * @return valor salvo.
     */
    public double getValueSaved() {
        return valueSaved;
    }

    /**
     * Define o valor salvo.
     * @param valueSaved novo valor.
     */
    public void setValueSaved(double valueSaved) {
        this.valueSaved = valueSaved;
    }

    /**
     * Retorna o tipo do dispositivo.
     * @return tipo do dispositivo.
     */
    public DeviceType getType() {
        return type;
    }

    /**
     * Define o tipo do dispositivo.
     * @param type novo tipo do dispositivo.
     */
    public void setType(DeviceType type) {
        this.type = type;
    }

    /**
     * Retorna o quarto do dispositivo.
     * @return quarto do dispositivo.
     */
    public Room getRoom() {
        return room;
    }

    public double getValue() {
        return value;
    }

    public int getNumNotifications(){
        return notifications.size();
    }

    public Notification getNotification(int position){
        return notifications.get(position);
    }

    public void addNotification(Notification newNotification){
        if (newNotification != null){
            notifications.add(newNotification);
        }
    }

    public void removeNotification(int position){
        notifications.remove(position);
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ArrayList<DataPointImpl> getDataPoints() {
        return dataPoints;
    }

    public void addDataPoint(DataPointImpl dataPoint){
        if(dataPoint != null){
            this.dataPoints.add(dataPoint);
            Collections.sort(dataPoints);
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