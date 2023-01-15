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
    protected boolean connectionState = true;
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

    public Device(){
    }

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
     * Método que retorna o valor do atributo "value"
     * @return double, representando o valor atual do dispositivo
     */
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    /**
     * Método que retorna o tamanho da lista de notificações
     * @return int, representando o número de notificações armazenadas
     */
    public int getNumNotifications(){
        return notifications.size();
    }

    /**
     * Método que retorna a notificação de uma determinada posição
     * @param position, int representando a posição desejada
     * @return Notification, representando a notificação na posição especificada
     */
    public Notification getNotification(int position){
        return notifications.get(position);
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }
    /**
     * Método que adiciona uma notificação a lista
     * @param newNotification, objeto da classe Notification a ser adicionado
     */
    public void addNotification(Notification newNotification){
        if (newNotification != null){
            notifications.add(newNotification);
        }
    }

    /**
     * Método que remove uma notificação de uma determinada posição
     * @param position, int representando a posição da notificação a ser removida
     */
    public void removeNotification(int position){
        notifications.remove(position);
    }
    /**
     * Método para setar a sala onde o dispositivo está contido.
     * @param room sala onde o dispositivo está contido.
     */
    public void set_Room(Room room) {
        this.room=room;
    }
    /**
     * Retorna o quarto do dispositivo.
     * @return quarto do dispositivo.
     */
    public Room getRoom() {
        return room;
    }
    /**
     * Método para obter a lista de pontos de dados do dispositivo.
     * @return lista de pontos de dados.
     */
    public ArrayList<DataPointImpl> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(ArrayList<DataPointImpl> dataPoints) {
        this.dataPoints = dataPoints;
    }
    /**
     * Método para adicionar um novo ponto de dado a lista de dados do dispositivo.
     * @param dataPoint novo ponto de dado a ser adicionado.
     */
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
    /**
     * Método para verificar se um objeto é igual a este objeto.
     * @param obj objeto a ser comparado.
     * @return true se os objetos são iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj.getClass() == this.getClass()) && this.channel == ((Device) obj).channel;
    }

    /**
     * Método para obter a representação em string do objeto.
     * @return string com o nome e o canal do dispositivo.
     */
    @Override
    public String toString() {
        return name + " - " + channel;
    }

    /**
     * Método para comparar este objeto com outro objeto do mesmo tipo.
     * @param device dispositivo a ser comparado.
     * @return 0 sempre que chamado.
     */
    @Override
    public int compareTo(Device device) {
        return device.getChannel() > this.channel ? 1 : -1;
    }
}