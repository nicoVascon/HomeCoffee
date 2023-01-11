package ipleiria.pdm.homecoffee.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Enums.RoomType;
import ipleiria.pdm.homecoffee.HouseManager;

public class Room implements Serializable, Comparable<Room> {

    private String Room_Name;
    //private String pathPhoto;
    private RoomType Room_Type;
    private ArrayList<Device> Devices;

    public Room() {
        // Default constructor required for calls to DataSnapshot.toObject(Room.class)
    }
    public Room( String Room_Name, RoomType Room_Type) {

        this.Room_Name = Room_Name;
        //this.pathPhoto = pathPhoto;
        this.Room_Type =Room_Type;
        this.Devices =new ArrayList<>();
    }

    public Room( String Room_Name, RoomType Room_Type,ArrayList<Device> devices) {

        this.Room_Name = Room_Name;
        //this.pathPhoto = pathPhoto;
        this.Room_Type =Room_Type;
        this.Devices =devices;
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
                                        roomDoc.getReference().update("Devices", Devices);

                                    }

                                }
                            }
                        });
                    }
                }


            }

        });


    }


    public void addDevice(Device dev){
        if(Devices.contains(dev)){
           return;
        }
        Devices.add(dev);
    }
    public ArrayList<Device> getDevices(){
        return Devices;
    }

    public RoomType getRoom_Type() {
        return Room_Type;
    }

    public void setRoom_Type(RoomType type) {
        this.Room_Type = type;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.Devices = devices;
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