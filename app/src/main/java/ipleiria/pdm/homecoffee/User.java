package ipleiria.pdm.homecoffee;

import java.io.Serializable;

public class User implements Serializable {
    private String email;

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

}
