package ipleiria.pdm.homecoffee.components.resources;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Date;

public class DataPointImpl extends DataPoint implements Comparable<DataPointImpl> {
    public DataPointImpl(double x, double y) {
        super(x, y);
    }

    public DataPointImpl(Date x, double y) {
        super(x, y);
    }

    @Override
    public int compareTo(DataPointImpl o) {
        if(o.getX() - this.getX() > 0){
            return -1;
        }
        return 1;
    }
}
