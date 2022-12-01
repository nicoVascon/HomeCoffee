package ipleiria.pdm.homecoffee.ui.Devices;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ipleiria.pdm.homecoffee.CircleSliderView;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Device;

public class DeviceDetailsFragment extends Fragment implements SaveData {
    private GraphView graphView;

    private Device selectedDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recoverData();
        return inflater.inflate(R.layout.fragment_device_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_devDetails));

        // on below line we are initializing our graph view.
        graphView = this.getView().findViewById(R.id.idGraphView);

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

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        graphView.setTitle("My Graph View");

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(18);

        // on below line we are adding
        // data series to our graph view.
        graphView.addSeries(series);

        CircleSliderView circleSliderView = getView().findViewById(R.id.circletimerview);

        circleSliderView.setOnTimeChangedListener(new CircleSliderView.OnTimeChangedListener() {
            @Override
            public void start(String starting) {
                System.out.println("Oiiiiiiii Starting " + starting);
            }

            @Override
            public void end(String ending) {
                System.out.println("Oiiiiiiii ending " + ending);
            }

            @Override
            public String setText(int value) {
                double percentValue = value*100 / MAX_VALUE;
                selectedDevice.setValue(percentValue);
                return String.format("%.2f", percentValue) + " %";
            }
        });
    }

    @Override
    public void saveData() {

    }

    @Override
    public void recoverData() {
        Bundle bundle = HouseManager.getBundle();
        int selectedDevPosition = bundle.getInt(DevicesFragment.RESULT_DEV_POSITION);
        this.selectedDevice = HouseManager.getInstance().getDevice(selectedDevPosition);
    }
}