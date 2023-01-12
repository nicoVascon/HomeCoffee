package ipleiria.pdm.homecoffee.model;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;
import java.util.Date;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;

/**
 * Classe Sensor, que representa um sensor.
 * Ela é uma extensão da classe Device
 */
public class Sensor extends Device{
    /**
     * Construtor da classe Sensor
     * @param channel canal do dispositivo
     * @param name nome do dispositivo
     * @param type tipo do dispositivo
     * @param room quarto onde o dispositivo se encontra
     */
    public Sensor(int channel, String name, DeviceType type, Room room) {
        super(channel, name, type, room);
    }

    /**
     * Método que atualiza o valor do sensor
     * @return retorna verdadeiro se o valor foi atualizado com sucesso
     */
    public boolean updateValue(){
        // Code for receive values from database
        double receivedValue = 25;
        this.value = receivedValue;
        //
        return true;
    }


    // Temporal method
    /**
     * Método temporal para definir o valor do sensor
     * @param value valor a ser definido
     */
    public void setValue(double value){
        this.value = value;
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        this.dataPoints.add(new DataPointImpl(currentDate, value));
        this.addNotification(new Notification(currentDate, "Novo Valor Recebido: " + String.format("%.2f", value)));
    }


}
