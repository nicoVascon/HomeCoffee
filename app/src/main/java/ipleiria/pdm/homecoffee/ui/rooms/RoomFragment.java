package ipleiria.pdm.homecoffee.ui.rooms;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

/**
 * Classe que representa o fragmento de uma sala
 * Estende a classe Fragment.
 */
public class RoomFragment extends Fragment {
    /**
     * Constante para a chave da posição da sala no resultado.
     */
    public static final String RESULT_ROOM_POSITION = "RESULT_ROOM_POSITION";
    /**
     * Constante para a chave da posição do dispositivo no resultado.
     */
    public static final String RESULT_DEV_POSITION = "RESULT_DEV_POSITION";

    /**
     * Variável que gerencia a casa.
     */
    private HouseManager houseManager;
    /**
     * Variável para o RecyclerView dos dispositivos.
     */
    private RecyclerView mRecyclerView;
    /**
     * Adaptador para os dispositivos.
     */
    private RecycleDevicesAdapter dAdapter;
    /**
     * Variável para o TextView do estado de conexão dos dispositivos.
     */
    private TextView txtDevConState;
    /**
     * Variável para o Switch de habilitação/desabilitação de todos os dispositivos.
     */
    private Switch allDevSwitch;
    /**
     * Variável para o botão de adição de dispositivos.
     */
    private Button addDeviceButton;
    /**
     * Variável estática que indica se os dispositivos estão habilitados.
     */
    private static boolean devicesEnable;
    /**
     * Variável que armazena a posição da sala.
     */
    private int room_position;


    /**
     * Método onCreateView() é chamado quando o fragmento é criado. Ele infla o layout do fragment_room
     * e o retorna como a visualização do fragmento.
     * @param inflater objeto LayoutInflater que é usado para inflar o layout do fragmento
     * @param container objeto ViewGroup que é o contêiner do fragmento
     * @param savedInstanceState objeto Bundle que contém o estado salvo anteriormente do fragmento
     * @return a visualização do fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    /**
     * Método onDestroyView() é chamado quando a visualização do fragmento é destruída.
     * Ele chama o método super.onDestroyView() da classe pai e adiciona o FragmentsEnum.ROOM_FRAGMENT
     * ao conjunto de fragmentos visitados.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.ROOM_FRAGMENT);
    }

    /**
     * Método onCreate() é chamado quando o fragmento é criado. Ele obtém a posição da sala
     * do Bundle passado como argumento e salva em uma variável room_position.
     * @param savedInstanceState objeto Bundle que contém o estado salvo anteriormente do fragmento
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        room_position = getArguments().getInt(RESULT_ROOM_POSITION);
        System.out.println(room_position);
    }

    /**
     *Método onStart é chamado quando o fragmento é iniciado.
     * Ele inicializa a RecyclerView, seta o título da toolbar e configura a funcionalidade do botão "Adicionar dispositivo" e do switch "Habilitar/Desabilitar dispositivos".
     */
    @Override
    public void onStart() {
        Context context = this.getContext();
        super.onStart();
        houseManager = HouseManager.getInstance();
        ArrayList<Device> devices = houseManager.getRoom(room_position).getDevices();

        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_devicesTitle));

        mRecyclerView = getView().findViewById(R.id.recyclerViewDevices);
        dAdapter = new RecycleDevicesAdapter(this.getActivity() ,this,devices){
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                Bundle bundle = HouseManager.getBundle();
                if (bundle == null){
                    bundle = new Bundle();
                    HouseManager.setBundle(bundle);
                }
                bundle.putInt(DevicesFragment.RESULT_DEV_POSITION, position);
                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new DeviceDetailsFragment()).commit();
            }
        };
        mRecyclerView.setAdapter(dAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        txtDevConState = getView().findViewById(R.id.textViewDevicesConState);
        allDevSwitch = getView().findViewById(R.id.switchAllDevState);
        allDevSwitch.setChecked(devicesEnable);
        allDevSwitch.setText(devicesEnable ? R.string.btn_enable_dev : R.string.btn_disabled_dev);

        allDevSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DevicesFragment.setDevicesEnable(isChecked);
                if (isChecked){
                    houseManager.recoverSavedDeviceConnectionState();
                }else{
                    houseManager.saveDeviceConnectionState();
                }

                dAdapter.notifyDataSetChanged();
                txtDevConState.setText(isChecked ? R.string.txt_DevicesConnected : R.string.txt_DevicesDisconnected);
                buttonView.setText(isChecked ? R.string.btn_enable_dev : R.string.btn_disabled_dev);
            }
        });

        addDeviceButton = getView().findViewById(R.id.btn_addDevice);
        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceSettingsFragment.editingDevice = false;
                ((MainActivity) dAdapter.getContext()).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new AddDeviceFragment()).commit();
            }
        });

        updateDevicesConnectionState();

    }

    /**
     * Método que atualiza o estado de conexão dos dispositivos.
     */
    public void updateDevicesConnectionState(){
        int numberOfDevicesConnect = houseManager.numberOfDevicesConnect();
        txtDevConState.setText(numberOfDevicesConnect > 0 ?
                numberOfDevicesConnect + getResources().getString(R.string.txt_DevicesConnected)  : getResources().getString(R.string.txt_DevicesDisconnected));
    }

    /**
     * Método que retorna se os dispositivos estão habilitados.
     * @return boolean devicesEnable
     */
    public static boolean isDevicesEnable() {
        return devicesEnable;
    }

    /**
     * Método que define se os dispositivos estão habilitados ou não.
     * @param devicesEnable
     */
    public static void setDevicesEnable(boolean devicesEnable) {
        RoomFragment.devicesEnable = devicesEnable;
    }

}