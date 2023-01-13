package ipleiria.pdm.homecoffee.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

//    public Room( String Room_Name, RoomType Room_Type,ArrayList<Device> devices) {
//
//        this.Room_Name = Room_Name;
//        //this.pathPhoto = pathPhoto;
//        this.Room_Type =Room_Type;
//        this.Devices =devices;
//    }

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



    public void updateRoomDev(){

        //Saving to Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userMail = HouseManager.getInstance().getUser().getEmail();
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("User_Email", userMail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (!result.isEmpty()) {
                        DocumentSnapshot userDoc = result.getDocuments().get(0);
                        CollectionReference roomsRef = userDoc.getReference().collection("rooms");
                        Query query = roomsRef.whereEqualTo("Room_Name", Room_Name);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot roomsSnapshot = task.getResult();
                                    if (!roomsSnapshot.isEmpty()) {
                                        DocumentSnapshot roomDoc = roomsSnapshot.getDocuments().get(0);
//                                        roomDoc.getReference().update("Devices", Devices);
                                        roomDoc.getReference().update("Sensors", Sensors);
                                        roomDoc.getReference().update("Actuators", Actuators);
                                    }

                                }
                            }
                        });
                    }
                }


            }

        });


    }


    public void removeRoomDev(){

        //Saving to Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userMail = HouseManager.getInstance().getUser().getEmail();
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("User_Email", userMail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (!result.isEmpty()) {
                        DocumentSnapshot userDoc = result.getDocuments().get(0);
                        CollectionReference roomsRef = userDoc.getReference().collection("rooms");
                        Query query = roomsRef.whereEqualTo("Room_Name", Room_Name);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot roomsSnapshot = task.getResult();
                                    if (!roomsSnapshot.isEmpty()) {
                                        DocumentSnapshot roomDoc = roomsSnapshot.getDocuments().get(0);
//                                        roomDoc.getReference().update("Devices", Devices);
                                        roomDoc.getReference().get(Source.valueOf("Sensors")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                List<Sensor> sensors = (List<Sensor>) documentSnapshot.get("Sensors");
                                                // make changes to the sensors list here
//                                                roomDoc.getReference().update("Sensors", sensors);
                                                System.out.println(sensors.toString());
                                            }
                                        });




//                                        roomDoc.getReference().update("Sensors", Sensors);
//                                        roomDoc.getReference().update("Actuators", Actuators);
                                    }

                                }
                            }
                        });
                    }
                }


            }

        });


    }


//    public void addDevice(Device dev){
//        if(Devices.contains(dev)){
//           return;
//        }
//        Devices.add(dev);
//    }

    public void addDevice(Device dev){
        if(dev instanceof Sensor){
            if(Sensors.contains(dev)){
                return;
            }
            Sensors.add((Sensor) dev);
        }else {
            if(Actuators.contains(dev)){
                return;
            }
            Actuators.add((Actuator) dev);
        }
    }

//    public ArrayList<Device> getDevices(){
//        return Devices;
//    }

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

//    public void setDevices(ArrayList<Device> devices) {
//        this.Devices = devices;
//    }


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