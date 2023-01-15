package ipleiria.pdm.homecoffee.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
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
     * Variável privada que armazena a referência no firebase do sensor associado a este atuador.
     */
    private DocumentReference associateddSensorRef;
    /**
     * Construtor vazio usado no processo que vai buscar dados á firebase
     */
    public Actuator(){
        this.notifications = new ArrayList<>();
        this.dataPoints = new ArrayList<>();
    }
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
            associatedSensor.setAssociatedActuator(this);

            CollectionReference refRooms = HouseManager.getInstance().getUser().getRoomsRef();
            DocumentReference refRealRoom= refRooms.document(sensor.getRoom().getRoom_Name());
            CollectionReference refSensors = refRealRoom.collection("Sensors");
            DocumentReference refSensor = refSensors.document(String.valueOf(sensor.getChannel()));
            setAssociateddSensorRef(refSensor);
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
    public double MeasuredValue(){
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

    public DocumentReference getAssociateddSensorRef() {
        return associateddSensorRef;
    }

    public void setAssociateddSensorRef(DocumentReference associateddSensorRef) {
        this.associateddSensorRef = associateddSensorRef;
    }

    @Override
    public void setConnectionState(boolean connectionState) {
        super.setConnectionState(connectionState);
        if (!connectionState){
            this.setValueSaved(this.getValue());
            this.setDesiredValue(0.0);
        }else{
            this.setDesiredValue(this.getValueSaved());
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
