package ipleiria.pdm.homecoffee.model;

import java.io.Serializable;
import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Enums.RoomType;
import ipleiria.pdm.homecoffee.HouseManager;

public class Room implements Serializable, Comparable<Room> {

    private String Room_Name;
    //private String pathPhoto;
    private RoomType Room_Type;
//    private ArrayList<Device> Devices;
    private ArrayList<Sensor> Sensors;
    private ArrayList<Actuator> Actuators;
    public Room() {
        // Default constructor required for calls to DataSnapshot.toObject(Room.class)
        this.Sensors = new ArrayList<>();
        this.Actuators = new ArrayList<>();
    }
    public Room( String Room_Name, RoomType Room_Type) {

        this.Room_Name = Room_Name;
        //this.pathPhoto = pathPhoto;
        this.Room_Type =Room_Type;
//        this.Devices =new ArrayList<>();
        this.Sensors = new ArrayList<>();
        this.Actuators = new ArrayList<>();
    }


    public Room( String Room_Name, RoomType Room_Type,ArrayList<Sensor> sensors, ArrayList<Actuator> actuators) {

        this.Room_Name = Room_Name;
        //this.pathPhoto = pathPhoto;
        this.Room_Type =Room_Type;
        this.Sensors =sensors;
        this.Actuators = actuators;
    }

    public String getRoom_Name() {
        return Room_Name;
    }




    public void updateRoomDevice(Sensor sensor){
        HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).collection("Sensors").document(String.valueOf(sensor.getChannel())).set(sensor);

    }

    public void updateRoomDev(){




        for (Sensor sensor : Sensors) {

            HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).collection("Sensors").document(String.valueOf(sensor.getChannel())).set(sensor);
        }

    }

    public void addDevice(Device dev){
        if(dev instanceof Sensor){
            if(Sensors.contains(dev)){
                return;
            }
            Sensors.add((Sensor) dev);
            HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name).collection("Sensors").document(String.valueOf(dev.getChannel())).set(dev);

        }else {
            if(Actuators.contains(dev)){
                return;
            }
            Actuators.add((Actuator) dev);
            HouseManager.getInstance().getUser().getRoomsRef().document(this.Room_Name ).collection("Actuators").document(String.valueOf(dev.getChannel())).set(dev);

        }
    }


    public ArrayList<Device> getDevices(){
        ArrayList<Device> result = new ArrayList<>(Sensors.size() + Actuators.size());
        result.addAll(Sensors);
        result.addAll(Actuators);
        return result;
    }

    public RoomType getRoom_Type() {
        return Room_Type;
    }

    public void setRoom_Type(RoomType type) {
        this.Room_Type = type;
    }


    public ArrayList<Sensor> getSensors() {
        return Sensors;
    }

    public void setSensors(ArrayList<Sensor> sensors) {
        this.Sensors = sensors;
    }

    public ArrayList<Actuator> getActuators() {
        return Actuators;
    }

    public void setActuators(ArrayList<Actuator> actuators) {
        this.Actuators = actuators;
    }

    @Override
    public boolean equals(Object obj) {
        return this.Room_Name == ((Room) obj).Room_Name;
    }

    @Override
    public String toString() {
        return Room_Name + " - " + Room_Type;
    }

    @Override
    public int compareTo(Room device) {
        return 0;
    }
}