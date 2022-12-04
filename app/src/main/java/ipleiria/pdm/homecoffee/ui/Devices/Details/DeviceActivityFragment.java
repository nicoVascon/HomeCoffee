package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class DeviceActivityFragment extends Fragment {
    Device selectedDevice;

    private GraphView lineChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_activity, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        int devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
        selectedDevice = HouseManager.getInstance().getDevice(devPosition);

        // on below line we are initializing our graph view.
        lineChart = this.getView().findViewById(R.id.lineChart_DevActivityFragment);

        // on below line we are adding data to our graph view.
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                // on below line we are adding
                // each point on our x and y axis.
                new DataPoint(0, 1),
                new DataPoint(1, 3),
                new DataPoint(2, 4),
                new DataPoint(3, 9),
                new DataPoint(4, 6),
                new DataPoint(5, 3),
                new DataPoint(6, 6),
                new DataPoint(7, 1),
                new DataPoint(8, 2)
        });
        lineChart.setTitle("Device Activity");

        // on below line we are setting
        // text color to our graph view.
        lineChart.setTitleColor(R.color.tabBarColor);

        // on below line we are setting
        // our title text size.
        lineChart.setTitleTextSize(18);

        // on below line we are adding
        // data series to our graph view.
        lineChart.addSeries(series);
    }
}