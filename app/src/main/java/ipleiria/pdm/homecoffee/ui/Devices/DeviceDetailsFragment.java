package ipleiria.pdm.homecoffee.ui.Devices;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Tests.Tab3Fragment;
import ipleiria.pdm.homecoffee.adapter.TabAdapter;
import ipleiria.pdm.homecoffee.components.CircleSliderView;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.components.resources.DataPointImpl;
import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceControlFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;

/**
 * Classe DeviceDetailsFragment que herda de Fragment e implementa a interface SaveData.
 * Essa classe é responsável por exibir os detalhes de um dispositivo selecionado, usando uma aba de navegação para exibir diferentes informações sobre o dispositivo.
 */
public class DeviceDetailsFragment extends Fragment implements SaveData {
    public static final int CONTROL_TAB_INDEX = 0;
    public static final int SETTINGS_TAB_INDEX = 2;
    public static boolean addAsVisitedFragment = true;
    /**
     * Guarda o dispositivo selecionado;
     */
    private static Device selectedDevice;

    /**
     * TabAdapter adapter: adaptador para a exibição das abas;
     */
    private TabAdapter adapter;
    /**
     * TabLayout tabLayout: gerencia as abas;
     */
    private TabLayout tabLayout;
    /**
     * ViewPager2 viewPager: gerencia a exibição das abas;
     */
    private ViewPager2 viewPager;
    /**
     * int initialTab: guarda a aba inicial que será exibida;
     */
    private int initialTab;

//    private String[] tabsTitles = {"Control", "Activity", "Schedule", "Settings"};
    /**
     * String[] tabsTitles: guarda os títulos das abas;
     */
    private String[] tabsTitles = {"Control", "Activity", "Settings"};
    /**
     * int[] tabsSelectedIcon: guarda os ícones selecionados das abas.
     */
    private int[] tabsSelectedIcon = {
            R.drawable.camera_tab_icon2,
            R.drawable.statistics_tab_icon,
            //R.drawable.schedule_tab_icon,
            R.drawable.settings_cute_tab_icon
    };

    /**
     * Construtor padrão para a classe DeviceDetailsFragment.
     */
    public DeviceDetailsFragment(){
        this(0);
    }

    /**
     * Construtor que permite definir a tab inicial que será exibida ao abrir o fragment.
     * @param initialTab índice da tab inicial que será exibida.
     */
    public DeviceDetailsFragment(int initialTab){
        super();
        this.initialTab = initialTab;
    }

    /**
     * Método chamado quando a view é criada, recupera os dados necessários para exibir o fragment.
     * @param savedInstanceState - Bundle contendo o estado salvo da instância anterior do fragmento.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Método chamado para criar a view do fragment.
     * @param inflater objeto para inflar o layout do fragment.
     * @param container ViewGroup onde o fragment será adicionado.
     * @param savedInstanceState dados salvos do fragment.
     * @return view criada para o fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recoverData();
        return inflater.inflate(R.layout.fragment_device_details, container, false);
    }

    /**
     * Este método é chamado quando este fragmento é criado.
     * Ele herda o método da classe mãe e o sobrescreve
     * com sua implementação específica. Ele seta o fragmento atual para este e seta o título da toolbar para
     * "devDetails" que é recuperado a partir dos recursos. Ele também imprime todos os pontos de dados selecionados
     * do dispositivo. Ele instancia e configura o viewPager, tabLayout, e o adapter. Ele também adiciona três fragmentos
     * DeviceControlFragment, DeviceActivityFragment, DeviceSettingsFragment ao adapter. Ele configura a cor de fundo do
     * tabLayout, a cor da guia selecionada, a cor do texto das guias, a cor do ícone das guias e adiciona um listener
     * ao tabLayout para controlar a cor do ícone da guia selecionada. Ele também adiciona uma descrição de conteúdo
     * opcional para o tabLayout e para a primeira guia. Ele verifica se há notificações para o dispositivo selecionado
     * e adiciona uma badge (contador) à segunda guia se houver.
     */
    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_devDetails));


//        Bundle bundle = HouseManager.getBundle();
//        if (bundle == null){
//            bundle = new Bundle();
//            HouseManager.setBundle(bundle);
//        }
//        bundle.putInt(DevicesFragment.RESULT_DEV_POSITION, 1);

        viewPager = getView().findViewById(R.id.viewPager);
        tabLayout = getView().findViewById(R.id.tabLayout);
        adapter = new TabAdapter(this);
        //adapter.addFragment(new Tab1Fragment(), "Tab 1");
        adapter.addFragment(new DeviceControlFragment(viewPager), "Tab 1");
//        adapter.addFragment(new Tab2Fragment(), "Tab 2");
        DeviceActivityFragment myDeviceActivityFragment = new DeviceActivityFragment();
        adapter.addFragment(myDeviceActivityFragment, "Tab 2");
        //adapter.addFragment(new Tab3Fragment(), "Tab 3");
//        adapter.addFragment(new Tab3Fragment(), "Tab 4");
        adapter.addFragment(new DeviceSettingsFragment(), "Tab 3");
        viewPager.setAdapter(adapter);

        tabLayout.setBackgroundColor(getResources().getColor(R.color.white));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_color));
        tabLayout.setTabTextColors(getResources().getColor(R.color.gray), getResources().getColor(R.color.app_color));
        tabLayout.setTabIconTint(null);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setIcon(tabsSelectedIcon[position]);
            tab.setText(tabsTitles[position]);
            if (position > 0){
                int tabIconColor = ContextCompat.getColor(this.getContext(), R.color.gray);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
        }).attach();


        Context context = this.getContext();
        viewPager.setUserInputEnabled(selectedDevice instanceof Sensor || !selectedDevice.isConnectionState());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(null);
                if (tab.getPosition() == 0){
//                    viewPager.setUserInputEnabled(false);
                    viewPager.setUserInputEnabled(selectedDevice instanceof Sensor ||
                            !selectedDevice.isConnectionState());
                }else{
                    viewPager.setUserInputEnabled(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(context, R.color.gray);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Optional :)
        String contentDescription = "Tab Layout Fixe";
        tabLayout.setContentDescription(contentDescription);
        String contentDescriptionTab1 = "Tab 1 Muito Fixe";
        tabLayout.getTabAt(0).setContentDescription(contentDescriptionTab1);

        if (selectedDevice.getNumNotifications() > 0){
            BadgeDrawable badge = tabLayout.getTabAt(1).getOrCreateBadge();
            badge.setNumber(selectedDevice.getNumNotifications());
            myDeviceActivityFragment.setListenner(new DeviceActivityFragment.onRecycleviewItemClickListenner() {
                @Override
                public void onDelete() {
                    int numNotifications = selectedDevice.getNumNotifications();
                    if (numNotifications > 0){
                        badge.setNumber(numNotifications);
                    }else{
                        tabLayout.getTabAt(1).removeBadge();
                    }
                }
            });
        }

        //Set initial Tab
        tabLayout.getTabAt(initialTab).select();
        viewPager.setCurrentItem(initialTab, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }
    /**
     * Classe responsável por salvar os dados do dispositivo.
     * @override Sobrescreve o método saveData() da classe pai
     */
    @Override
    public void saveData() {
        initialTab = viewPager.getCurrentItem();
    }

    /**
     * Classe responsável por recuperar os dados do dispositivo.
     * @override Sobrescreve o método recoverData() da classe pai.
     */
    @Override
    public void recoverData() {
//        Bundle bundle = HouseManager.getBundle();
//        int selectedDevPosition = bundle.getInt(DevicesFragment.RESULT_DEV_POSITION);
//
//        ArrayList<Device> devicesArrayList = HouseManager.getInstance().getDevices();
//        if(HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
//            int roomPosition = HouseManager.getBundle().getInt(HomeFragment.RESULT_ROOM_POSITION);
//            Room room = HouseManager.getInstance().getRoom(roomPosition);
//            devicesArrayList = room.getDevices();
//            selectedDevPosition = bundle.getInt(RoomFragment.RESULT_DEV_POSITION);
//        }
//        this.selectedDevice = devicesArrayList.get(selectedDevPosition);
    }

    public static void setSelectedDevice(Device selectedDevice) {
        DeviceDetailsFragment.selectedDevice = selectedDevice;
    }

    public static Device getSelectedDevice() {
        return selectedDevice;
    }

    /**
     * Classe responsável por destruir a view do dispositivo.
     * @override Sobrescreve o método onDestroyView() da classe pai.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(!addAsVisitedFragment){
            addAsVisitedFragment = true;
            return;
        }
        MainActivity.addFragmentViseted(FragmentsEnum.DEVICE_DETAILS_FRAGMENT);
        if(HouseManager.getBundle() != null && HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
            HouseManager.getBundle().remove(HomeFragment.RESULT_ROOM_POSITION);
        }
    }
}