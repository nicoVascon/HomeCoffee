package ipleiria.pdm.homecoffee.components.resources;

import com.jjoe64.graphview.series.DataPointInterface;

import java.io.Serializable;
import java.util.Date;

public class DataPointImpl implements DataPointInterface, Serializable, Comparable<DataPointImpl> {
    private static final long serialVersionUID=1428263322645L;

    private double x;
    private double y;

    public DataPointImpl(){}

    public DataPointImpl(double x, double y) {
        this.x=x;
        this.y=y;
    }

    public DataPointImpl(Date x, double y) {
        this.x = x.getTime();
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "["+x+"/"+y+"]";
    }

    @Override
    public int compareTo(DataPointImpl o) {
        if(o.getX() - this.getX() > 0){
            return -1;
        }
        return 1;
    }
}
