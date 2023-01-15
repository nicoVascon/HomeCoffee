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
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;

/**
 * Classe que representa o fragmento de exibição de dados de um dispositivo selecionado.
 *
 * Possibilita a visualização de gráficos dos dados do dispositivo, e de notificações relacionadas ao mesmo.
 */
public class DeviceActivityFragment extends Fragment {
    /**
     * Constante que define o número máximo de etiquetas no eixo x.
     */
    public static final int MAX_NUM_LABELS = 5;
    /**
     * Constante que define o intervalo da escala no eixo x para a visualização semanal.
     */
    public static final int X_AXIS_INTERVAL_WEEK = 0;
    /**
     * Constante que define o intervalo da escala no eixo x para a visualização diária.
     */
    public static final int X_AXIS_INTERVAL_DAY = 1;
    /**
     * Constante que define o intervalo da escala no eixo x para a visualização horária.
     */
    public static final int X_AXIS_INTERVAL_HOUR = 2;
    /**
     * Dispositivo selecionado
     */
    private Device selectedDevice;
    /**
     * Dados do gráfico.
     */
    private static DataPointImpl[] dataPoints1;
    /**
     * Legenda do gráfico
     */
    private static String legend;
    //private DataPoint[] dataPoints2;

    /**
     * Variável estática que contém a instância de gráfico personalizado (lineChart)
     * para exibir as informações do dispositivo selecionado
     */
    private static GraphView_Custom lineChart;
    /**
     * Variável que contém a instância do botão "week" para filtrar as informações do dispositivo selecionado por semana
     */
    private Button btn_week;
    /**
     * Variável que contém a instância do botão "day" para filtrar as informações do dispositivo selecionado por dia
     */
    private Button btn_day;
    /**
     * Variável que contém a instância do botão "hour" para filtrar as informações do dispositivo selecionado por hora
     */
    private Button btn_hour;
    /**
     * Variável que contém a instância do RecyclerView para exibir as notificações relacionadas ao dispositivo selecionado
     */
    private RecyclerView mRecyclerView;
    /**
     * Variável que contém a instância do adapter personalizado (dAdapter)
     * para o RecyclerView de notificações relacionadas ao dispositivo selecionado
     */
    private RecycleNotificationsAdapter dAdapter;
    /**
     * Variável que contém a instância de uma classe personalizada (listenner) para
     * tratar os eventos de clique do RecyclerView de notificações relacionadas ao dispositivo selecionado
     */
    private onRecycleviewItemClickListenner listenner;

    /**
     * Variável estática que contém a instância de uma classe personalizada (labelFormatter)
     * para formatar as labels do eixo x no gráfico do dispositivo selecionado
     */
    private static LabelFormatter labelFormatter;

    /**
     * O método onCreate é chamado quando o fragmento é criado. Ele é usado para inicializar quaisquer variáveis ​​que precisem ser configuradas no momento de criação do fragmento.
     * @param savedInstanceState salva o estado do aplicativo anteriormente salvo.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * O método onCreateView é chamado quando o sistema cria a visualização do fragmento. Ele é usado para inflar a visualização para o fragmento, além de recuperar qualquer estado de visualização salvo anteriormente.
     * @param inflater objeto que é usado para inflatar a visualização.
     * @param container o container pai onde a visualização do fragmento será adicionada.
     * @param savedInstanceState salva o estado do aplicativo anteriormente salvo.
     * @return view a visualização inflada para o fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_activity, container, false);
    }

    /**
     * Método do ciclo de vida do fragmento chamado quando o fragmento é iniciado.
     * Aqui são inicializadas as views e configuradas as ações dos botões, além disso,
     * é verificado se o dispositivo selecionado possui notificações e é configurado o adapter do recycleView de notificações
     */
    @Override
    public void onStart() {
        super.onStart();

//        if(HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
//            int roomPosition = HouseManager.getBundle().getInt(HomeFragment.RESULT_ROOM_POSITION);
//            Room room = HouseManager.getInstance().getRoom(roomPosition);
//            int devPosition = HouseManager.getBundle().getInt(RoomFragment.RESULT_DEV_POSITION);
//            selectedDevice = room.getDevices().get(devPosition);
//        }else{
//            int devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
//            selectedDevice = HouseManager.getInstance().getDevice(devPosition);
//        }

        this.selectedDevice = DeviceDetailsFragment.getSelectedDevice();

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

    /**
     * Este método configura o intervalo do eixo X do gráfico, baseado no parâmetro passado.
     * Seleciona-se a data mais recente, e com base no intervalo selecionado (semana, dia ou hora)
     * as informações do dispositivo são filtradas e atualizadas no gráfico.
     * @param interval inteiro representando o intervalo a ser selecionado
     */
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

    /**
     * Método estático que atualiza os valores do gráfico.
     * Verifica se o lineChart é nulo, se for, o método retorna sem fazer nada.
     * Caso contrário, o método chama o método init do lineChart e o método initGraph.
     */
    public static void updateValues(){
        if(lineChart == null){
            return;
        }
        lineChart.init();
        initGraph(lineChart);
    }

    /**
     * Método estático que inicializa o gráfico.
     * @param graph A view do gráfico.
     */
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

    /**
     *Método que retorna o listenner de clique para o recycleview.
     * @return O listenner de clique para o recycleview.
     */
    public onRecycleviewItemClickListenner getListenner() {
        return listenner;
    }

    /**
     *  Método que define o listener de clique para o recycleview
     * @param listenner que ficará vinculado ao click da recycle view
     */
    public void setListenner(onRecycleviewItemClickListenner listenner) {
        this.listenner = listenner;
    }

    /**
     * Método chamado quando a view é destruída.
     * Ele remove a referência ao gráfico, legenda, dados e formato de rótulo.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DeviceActivityFragment.lineChart = null;
        DeviceActivityFragment.legend = null;
        DeviceActivityFragment.dataPoints1 = null;
        DeviceActivityFragment.labelFormatter = null;
    }

    /**
     * Interface para escutar os eventos de clique no recyclerview.
     *
     */
    public interface onRecycleviewItemClickListenner {
        void onDelete();
    }
}