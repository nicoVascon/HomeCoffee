package ipleiria.pdm.homecoffee.model;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

/**
 * Classe que representa um actuador, herda de Device
 */
public class Actuator extends Device{

    /**
     * Variável privada que armazena o sensor associado a este atuador.
     */
    private Sensor associatedSensor;

    /**
     * Construtor da classe
     * @param channel canal usado para comunicação com o dispositivo
     * @param name nome dado ao dispositivo
     * @param type tipo do dispositivo
     * @param room sala onde o dispositivo está localizado
     */
    public Actuator(int channel, String name, DeviceType type, Room room) {
        super(channel, name, type, room);
    }

    /**
     * Método para setar o sensor associado a este atuador
     * @param sensor o sensor associado, pode ser null
     * @return o sensor associado
     */
    public Sensor setAssociatedSensor(@Nullable Sensor sensor){
        if (sensor == null){
            associatedSensor = null;
        }
        if(sensor.getType() == this.type){
            associatedSensor = sensor;
            this.dataPoints = sensor.dataPoints;
        }
        return associatedSensor;
    }

    /**
     * Método para recuperar o sensor associado a este atuador
     * @return o sensor associado
     */
    public Sensor getAssociatedSensor() {
        return associatedSensor;
    }

    /**
     * Método para recuperar o valor medido pelo sensor associado
     * @return valor medido pelo sensor associado
     */
    public double getMeasuredValue(){
        return this.associatedSensor.getValue();
    }

    /**
     * Método para setar o valor desejado do atuador
     * @param value o valor desejado
     */
    public void setDesiredValue(double value){
        if(sendValueChangeCommand(value)){
            this.value = value;
            if(associatedSensor == null){
                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();
                this.dataPoints.add(new DataPointImpl(currentDate, value));
            }
        }
    }

    /**
     * Método sendValueChangeCommand é responsável por enviar um comando de mudança de valor para o dispositivo ESP32.
     * Ele verifica se o tipo do dispositivo é digital ou de luminosidade, e envia o valor inteiro para o ESP32.
     * Caso contrário, envia o valor double.
     * @param newValue é o novo valor que o dispositivo deve assumir.
     * @return retorna verdadeiro caso o comando tenha sido enviado com sucesso.
     */
    private boolean sendValueChangeCommand(double newValue){
        // Code for App-ESP32 communication
        if(this.type == DeviceType.DIGITAL || this.type == DeviceType.LUMINOSITY){
            HouseManager.addString_send_ttn(this.channel, this.channel + "," + ((int) newValue) );
        }else{
            HouseManager.addString_send_ttn(this.channel, this.channel + "," + newValue );
        }

        return true;
    }
}
