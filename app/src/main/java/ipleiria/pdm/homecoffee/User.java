package ipleiria.pdm.homecoffee;

import com.google.firebase.firestore.CollectionReference;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private CollectionReference roomsRef;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CollectionReference getRoomsRef() {
        return roomsRef;
    }

    public void setRoomsRef(CollectionReference roomsRef) {
        this.roomsRef = roomsRef;
    }
}
