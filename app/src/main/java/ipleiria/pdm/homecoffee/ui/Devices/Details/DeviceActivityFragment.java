package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.adapter.RecycleNotificationsAdapter;
import ipleiria.pdm.homecoffee.adapter.TabAdapter;
import ipleiria.pdm.homecoffee.components.GraphView_Custom;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

public class DeviceActivityFragment extends Fragment {
    public static final int MAX_NUM_LABELS = 5;
    public static final int X_AXIS_INTERVAL_WEEK = 0;
    public static final int X_AXIS_INTERVAL_DAY = 1;
    public static final int X_AXIS_INTERVAL_HOUR = 2;

    private Device selectedDevice;
    private DataPoint[] dataPoints1;
    private DataPoint[] dataPoints2;
    // Temporal Attributes
    private DataPoint[] original_dataPoints1;
    private DataPoint[] original_dataPoints2;

    private GraphView_Custom lineChart;
    private Button btn_week;
    private Button btn_day;
    private Button btn_hour;

    private RecyclerView mRecyclerView;
    private RecycleNotificationsAdapter dAdapter;
    private onRecycleviewItemClickListenner listenner;

    private LabelFormatter labelFormatter;

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

        TextView txtNoNotificationsMessage = getView().findViewById(R.id.textViewNoNotificationsMessage);
        if(selectedDevice.getNumNotifications() > 0){
            txtNoNotificationsMessage.setVisibility(View.GONE);
        }else{
            txtNoNotificationsMessage.setVisibility(View.VISIBLE);
        }

        mRecyclerView = getView().findViewById(R.id.recyclerViewNotifications);
        dAdapter = new RecycleNotificationsAdapter(this.getContext()){
            @Override
            public void onDeleteClick(int position) {
                super.onDeleteClick(position);
                listenner.onDelete();
                if(selectedDevice.getNumNotifications() > 0){
                    txtNoNotificationsMessage.setVisibility(View.GONE);
                }else{
                    txtNoNotificationsMessage.setVisibility(View.VISIBLE);
                }
            }
        };
        mRecyclerView.setAdapter(dAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        lineChart = getView().findViewById(R.id.lineChart_DevActivityFragment);
        btn_week = getView().findViewById(R.id.btn_week);
        btn_day = getView().findViewById(R.id.btn_day);
        btn_hour = getView().findViewById(R.id.btn_hour);

        btn_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAxisXInterval(X_AXIS_INTERVAL_WEEK);
            }
        });
        btn_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAxisXInterval(X_AXIS_INTERVAL_DAY);
            }
        });
        btn_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAxisXInterval(X_AXIS_INTERVAL_HOUR);
            }
        });

        // generate Dates
        Calendar calendar = Calendar.getInstance();
        Date d5 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d4 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d1 = calendar.getTime();
        // Points in different minutes
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -15);
        Date dm3 = calendar.getTime();
        calendar.add(Calendar.MINUTE, -24);
        Date dm2 = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date dm1 = calendar.getTime();

        dataPoints1 = new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 5),
                new DataPoint(d3, 3),
                new DataPoint(d4, 2),

                new DataPoint(dm1, 10),
                new DataPoint(dm2, 6),
                new DataPoint(dm3, 1),

                new DataPoint(d5, 6)
        };

        dataPoints2 = new DataPoint[] {
                new DataPoint(d1, 3),
                new DataPoint(d2, 3),
                new DataPoint(d3, 6),
                new DataPoint(d4, 2),

                new DataPoint(dm1, 8),
                new DataPoint(dm2, 4),
                new DataPoint(dm3, -1),

                new DataPoint(d5, 5)
        };

        original_dataPoints1 = dataPoints1;
        original_dataPoints2 = dataPoints2;

        labelFormatter = new DateAsXAxisLabelFormatter(lineChart.getContext());
        initGraph(lineChart);
    }

    public void setAxisXInterval(int interval){
        ArrayList<DataPoint> arrayList_dataPoints1 = new ArrayList<>();
        ArrayList<DataPoint> arrayList_dataPoints2 = new ArrayList<>();
        Date minDate;
        Calendar currentTime = Calendar.getInstance();

        switch (interval){
            case X_AXIS_INTERVAL_WEEK:
                currentTime.add(Calendar.WEEK_OF_MONTH, -1);
                labelFormatter = new DateAsXAxisLabelFormatter(lineChart.getContext());
                break;
            case X_AXIS_INTERVAL_DAY:
                currentTime.add(Calendar.DAY_OF_MONTH, -2);
                labelFormatter = new DateAsXAxisLabelFormatter(lineChart.getContext());
                break;
            case X_AXIS_INTERVAL_HOUR:
                currentTime.add(Calendar.HOUR_OF_DAY, -1);
                labelFormatter = new DateAsXAxisLabelFormatter(lineChart.getContext(), new SimpleDateFormat("HH:mm:ss"));
                System.out.println("Oiiiiii Hora: " + (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTimeInMillis()));
                break;
            default:
                return;
        }

        minDate = currentTime.getTime();
        for(int i = 0; i < original_dataPoints1.length; i++){
            if (original_dataPoints1[i].getX() >= minDate.getTime()){
                arrayList_dataPoints1.add(original_dataPoints1[i]);
            }
        }
        for(int i = 0; i < original_dataPoints2.length; i++){
            if (original_dataPoints2[i].getX() >= minDate.getTime()){
                arrayList_dataPoints2.add(original_dataPoints2[i]);
            }
        }

        dataPoints1 = new DataPoint[arrayList_dataPoints1.size()];
        dataPoints2 = new DataPoint[arrayList_dataPoints2.size()];
        for(int i = 0; i < dataPoints1.length; i++){
            dataPoints1[i] = arrayList_dataPoints1.get(i);
        }
        for(int i = 0; i < dataPoints2.length; i++){
            dataPoints2[i] = arrayList_dataPoints2.get(i);
        }
        //lineChart.removeAllSeries();
        lineChart.init();
        initGraph(lineChart);
    }

    public void initGraph(GraphView graph) {
        // first series
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints1);
        series.setTitle("Electric Consume");
        graph.addSeries(series);

        // second series
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(dataPoints2);
        series2.setTitle("Temperature");
        series2.setDrawBackground(true);
        series2.setColor(Color.argb(255, 255, 60, 60));
        series2.setBackgroundColor(Color.argb(100, 204, 119, 119));
        series2.setDrawDataPoints(true);
        graph.addSeries(series2);

        // legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(labelFormatter);
        int maxNumElemets = Math.max(dataPoints1.length, dataPoints2.length);
        int mNumLabels = Math.min(maxNumElemets, MAX_NUM_LABELS);
        graph.getGridLabelRenderer().setNumHorizontalLabels(mNumLabels);
        graph.getGridLabelRenderer().setTextSize(20f);

        double minXLabelValue = Double.MAX_VALUE;
        double maxXLabelValue = 0;
        for(int i = 0; i < dataPoints1.length; i++){
            minXLabelValue = Math.min(dataPoints1[i].getX(), minXLabelValue);
            maxXLabelValue = Math.max(dataPoints1[i].getX(), maxXLabelValue);
        }
        for(int i = 0; i < dataPoints2.length; i++){
            minXLabelValue = Math.min(dataPoints2[i].getX(), minXLabelValue);
            maxXLabelValue = Math.max(dataPoints2[i].getX(), maxXLabelValue);
        }

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(minXLabelValue);
        graph.getViewport().setMaxX(maxXLabelValue);
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not nessecary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    public onRecycleviewItemClickListenner getListenner() {
        return listenner;
    }

    public void setListenner(onRecycleviewItemClickListenner listenner) {
        this.listenner = listenner;
    }

    public interface onRecycleviewItemClickListenner {
        void onDelete();
    }
}