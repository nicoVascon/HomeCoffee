package ipleiria.pdm.homecoffee.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification implements Serializable, Comparable<Notification> {
    private Date date;
    private String description;

    public Notification(){

    }

    public Notification(Date date, String description){
        this.date = date;
        this.description = description;
    }

    public Notification(String description){
        this(new Date(), description); // Get the current time
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateFormatted(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormatter.format(date);
    }

    @Override
    public int compareTo(Notification o) {
        if(o.date.getTime() > this.date.getTime()){
            return 1;
        }
        return -1;
    }
}
