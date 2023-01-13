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
import java.util.Collections;
import java.util.Date;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.adapter.RecycleNotificationsAdapter;
import ipleiria.pdm.homecoffee.adapter.TabAdapter;
import ipleiria.pdm.homecoffee.components.GraphView_Custom;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;

public class DeviceActivityFragment extends Fragment {
    public static final int MAX_NUM_LABELS = 5;
    public static final int X_AXIS_INTERVAL_WEEK = 0;
    public static final int X_AXIS_INTERVAL_DAY = 1;
    public static final int X_AXIS_INTERVAL_HOUR = 2;

    private Device selectedDevice;
    private static DataPointImpl[] dataPoints1;
    private static String legend;
    //private DataPoint[] dataPoints2;

    private static GraphView_Custom lineChart;
    private Button btn_week;
    private Button btn_day;
    private Button btn_hour;

    private RecyclerView mRecyclerView;
    private RecycleNotificationsAdapter dAdapter;
    private onRecycleviewItemClickListenner listenner;

    private static LabelFormatter labelFormatter;

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

        if(HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
            int roomPosition = HouseManager.getBundle().getInt(HomeFragment.RESULT_ROOM_POSITION);
            Room room = HouseManager.getInstance().getRoom(roomPosition);
            int devPosition = HouseManager.getBundle().getInt(RoomFragment.RESULT_DEV_POSITION);
            selectedDevice = room.getDevices().get(devPosition);
        }else{
            int devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
            selectedDevice = HouseManager.getInstance().getDevice(devPosition);
        }

        TextView txtNoNotificationsMessage = getView().findViewById(R.id.textViewNoNotificationsMessage);
        if(selectedDevice.getNumNotifications() > 0){
            txtNoNotificationsMessage.setVisibility(View.GONE);
        }else{
            txtNoNotificationsMessage.setVisibility(View.VISIBLE);
        }

        mRecyclerView = getView().findViewById(R.id.recyclerViewNotifications);
        dAdapter = new RecycleNotificationsAdapter(this.getContext(), selectedDevice){
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

        legend = getContext().getResources().getString(R.string.txt_MesuredValue);

        switch (selectedDevice.getType()){
            case DIGITAL:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Digital);
                break;
            case ANALOG:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Analog);
                break;
            case PRESENCE:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Presence);
                break;
            case HUMIDITY:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Humidity);
                break;
            case TEMPERATURE:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Temperature);
                break;
            case LUMINOSITY:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Light);
                break;
            case ACCELERATION:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Acceleration);
                break;
            case PRESSURE:
                legend = getContext().getResources().getString(R.string.deviceTypeName_Pressure);
                break;
        }

        if(selectedDevice.getDataPoints().size() > 1){
            dataPoints1 = new DataPointImpl[selectedDevice.getDataPoints().size()];
            for(int i = 0; i < dataPoints1.length; i++){
                dataPoints1[i] = selectedDevice.getDataPoints().get(i);
            }
        }else{
            dataPoints1 = new DataPointImpl[1];
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            dataPoints1[0] = new DataPointImpl(currentDate, 0);
        }

        labelFormatter = new DateAsXAxisLabelFormatter(lineChart.getContext());
        initGraph(lineChart);
    }

    public void setAxisXInterval(int interval){
        if(selectedDevice.getDataPoints().size() == 0){
            return;
        }

        ArrayList<DataPointImpl> arrayList_dataPoints2 = new ArrayList<>();
        ArrayList<DataPointImpl> arrayList_dataPoints1 = new ArrayList<>();
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
        ArrayList<DataPointImpl> deviceDataPoints = selectedDevice.getDataPoints();
        for(int i = 0; i < deviceDataPoints.size(); i++){
            if (deviceDataPoints.get(i).getX() >= minDate.getTime()){
                arrayList_dataPoints1.add(deviceDataPoints.get(i));
            }
        }

        if(!arrayList_dataPoints1.isEmpty()){
            dataPoints1 = new DataPointImpl[arrayList_dataPoints1.size()];
            //dataPoints2 = new DataPoint[arrayList_dataPoints2.size()];
            for(int i = 0; i < dataPoints1.length; i++){
                dataPoints1[i] = arrayList_dataPoints1.get(i);
            }
        }else{
            dataPoints1 = new DataPointImpl[1];
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            dataPoints1[0] = new DataPointImpl(currentDate, 0);
        }
        lineChart.init();
        initGraph(lineChart);
    }

    public static void updateValues(){
        if(lineChart == null){
            return;
        }
        lineChart.init();
        initGraph(lineChart);
    }

    public static void initGraph(GraphView graph) {
//        // first series
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints2);
//        series.setTitle("Electric Consume");
//        graph.addSeries(series);

        // second series
        LineGraphSeries<DataPointImpl> series2 = new LineGraphSeries<>(dataPoints1);
        series2.setTitle(legend);
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
        //int maxNumElemets = Math.max(dataPoints1.length, dataPoints2.length);
        int maxNumElemets = dataPoints1.length;
        int mNumLabels = Math.min(maxNumElemets, MAX_NUM_LABELS);
        graph.getGridLabelRenderer().setNumHorizontalLabels(mNumLabels);
        graph.getGridLabelRenderer().setTextSize(20f);

        double minXLabelValue = Double.MAX_VALUE;
        double maxXLabelValue = 0;
        for(int i = 0; i < dataPoints1.length; i++){
            minXLabelValue = Math.min(dataPoints1[i].getX(), minXLabelValue);
            maxXLabelValue = Math.max(dataPoints1[i].getX(), maxXLabelValue);
        }
//        for(int i = 0; i < dataPoints2.length; i++){
//            minXLabelValue = Math.min(dataPoints2[i].getX(), minXLabelValue);
//            maxXLabelValue = Math.max(dataPoints2[i].getX(), maxXLabelValue);
//        }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DeviceActivityFragment.lineChart = null;
        DeviceActivityFragment.legend = null;
        DeviceActivityFragment.dataPoints1 = null;
        DeviceActivityFragment.labelFormatter = null;
    }

    public interface onRecycleviewItemClickListenner {
        void onDelete();
    }
}