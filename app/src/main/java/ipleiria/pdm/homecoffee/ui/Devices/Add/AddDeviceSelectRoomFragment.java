package ipleiria.pdm.homecoffee.ui.Devices.Add;

import static ipleiria.pdm.homecoffee.Enums.FragmentsEnum.ADD_DEVICES_SELECT_ROOM_FRAGMENT;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceSettingsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;
/**
 * Classe responsável pelo fragmento de seleção de quarto para adição de novo dispositivo.
 * Extende a classe Fragment e implementa a interface SaveData.
 * Utiliza um RecyclerView para exibir os quartos cadastrados e possibilita a escolha de um para associar ao novo dispositivo.
 * Tem a possibilidade de adicionar ou editar um dispositivo.
 */
public class AddDeviceSelectRoomFragment extends Fragment implements SaveData {
    public static final String RESULT_NEW_DEV_ROOM = "RESULT_NEW_DEV_ROOM";
    /**
     * RecyclerView utilizado para exibir a lista de quartos cadastrados
     */
    private RecyclerView mRecyclerView;
    /**
     * Adapter do RecyclerView
     */
    private RecycleRoomsAdapter mAdapter;
    /**
     * Botão utilizado para adicionar ou editar um dispositivo
     */
    private Button addButton;
    /**
     * Armazena o nome do novo dispositivo
     */
    private String newDevName;
    /**
     * Armazena o canal do novo dispositivo
     */
    private int newDevChannel;
    /**
     * Armazena o tipo do novo dispositivo
     */
    private DeviceType newDevType;
    /**
     * Armazena o quarto do novo dispositivo
     */
    private Room newDevRoom;
    /**
     * Armazena o modo do novo dispositivo
     */
    private int newDevMode;
    /**
     * Armazena o sensor a ser associado ao dispositivo
     */
    private Sensor sensorToAssociate;

    /**
     * Cria e retorna a view do fragmento.
     * Recupera os dados armazenados anteriormente.
     * @param inflater LayoutInflater utilizado para inflar a view
     * @param container ViewGroup container
     * @param savedInstanceState Bundle com dados salvo anteriormente
     * @return view criada
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recoverData();
        return inflater.inflate(R.layout.fragment_add_device_select_room, container, false);
    }

    /**
     * Configura o fragmento quando ele é iniciado.
     * Define o fragmento atual na MainActivity e define o título da toolbar.
     * Configura o botão de adição/edição de dispositivo para chamar o método correspondente.
     */
    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_addDevTitle));

        addButton = getView().findViewById(R.id.button_devAdd_SelectRoom);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceSettingsFragment.editingDevice){
                    edit();
                }else {
                    add();
                }
            }
        });
        if(DeviceSettingsFragment.editingDevice){
            addButton.setText(getResources().getString(R.string.btn_save));
            MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_editDevTitle));
        }else {
            addButton.setText(getResources().getString(R.string.btn_addDevice));
        }
        mRecyclerView = getView().findViewById(R.id.RecyclerViewAddDevSelectRoom);
        mAdapter = new RecycleRoomsAdapter(this.getActivity()){
            @Override
            public void onBindViewHolder(@NonNull RoomsHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                CardView cardView = holder.itemView.findViewById(R.id.cardViewRoomItem);
                cardView.setCardBackgroundColor(getResources().getColor(R.color.iconBackgoundRooms2));

                Bundle mbundle = HouseManager.getBundle();
                if(mbundle.containsKey(RESULT_NEW_DEV_ROOM)){
                    int lastSelectedRoomPosition = mbundle.getInt(RESULT_NEW_DEV_ROOM);
                    if (lastSelectedRoomPosition == position){
                        onItemClick(holder.itemView, position);
                    }
                }
            }

            @Override
            public void onItemClick(View v, int position){
                super.onItemClick(v, position);
                newDevRoom = HouseManager.getInstance().getRoom(position);
                Bundle mbundle = HouseManager.getBundle();
                mbundle.putInt(RESULT_NEW_DEV_ROOM, position);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));
    }

    /**
     * Método responsável por editar o dispositivo selecionado.
     * Ele verifica se a nova sala do dispositivo foi definida e, se sim,
     * atualiza as informações do dispositivo selecionado e redireciona para o fragmento de detalhes do dispositivo.
     * Caso contrário, exibe uma mensagem de erro informando que a sala do dispositivo não foi selecionada.
     */
    private void edit(){
        if (newDevRoom != null){
            Device selectedDevice = null;
            ArrayList<Device> deviceArrayList = null;
            if(HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
                int roomPosition = HouseManager.getBundle().getInt(HomeFragment.RESULT_ROOM_POSITION);
                Room room = HouseManager.getInstance().getRoom(roomPosition);
                int devPosition = HouseManager.getBundle().getInt(RoomFragment.RESULT_DEV_POSITION);
                selectedDevice = room.getDevices().get(devPosition);
                deviceArrayList = room.getDevices();
            }else{
                int devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
                selectedDevice = HouseManager.getInstance().getDevice(devPosition);
                deviceArrayList = HouseManager.getInstance().getDevices();
            }
            selectedDevice.setName(newDevName);
            selectedDevice.setChannel(newDevChannel);
            selectedDevice.setType(newDevType);
            selectedDevice.set_Room(newDevRoom);
            selectedDevice.update();

            HouseManager.setBundle(null);
            MainActivity.clearFragmentsVisitedList();
            DeviceDetailsFragment.setSelectedDevice(selectedDevice);
            ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new DeviceDetailsFragment(DeviceDetailsFragment.SETTINGS_TAB_INDEX)).commit();
            return;
        }
        Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevRoom, Toast.LENGTH_LONG).show();
    }
    /**
     * Método responsável por adicionar um dispositivo. Ele verifica se a nova sala foi selecionada e então adiciona um dispositivo
     * de acordo com o modo escolhido. Caso o modo seja 0, é adicionado um sensor, caso contrário é adicionado um atuador.
     * Caso haja um sensor associado, ele é associado ao atuador recém criado.
     * Em caso de sucesso na adição do dispositivo, é exibida uma mensagem de sucesso.
     * Em caso de falha, é exibida uma mensagem de falha.
     * Se a sala não foi selecionada, é exibida uma mensagem informando a falta da seleção.
     * 
     */
    private void add(){
        if (newDevRoom != null){
            if(newDevMode == 0) {
                Sensor newSensor = new Sensor(newDevChannel, newDevName, newDevType, newDevRoom);
                HouseManager.getInstance().addDevice(newSensor);
                newDevRoom.addDevice(newSensor);
                newDevRoom.updateRoomDev();
            }else{
                Actuator newActuator = new Actuator(newDevChannel, newDevName, newDevType, newDevRoom);
                HouseManager.getInstance().addDevice(newActuator);
//
//                newDevRoom.updateRoomDev();
                if(sensorToAssociate != null){
                    Sensor currentAssociatedSensor = newActuator.setAssociatedSensor(sensorToAssociate);
                    if(currentAssociatedSensor == sensorToAssociate){
                        Toast.makeText(getContext(), getResources().getString(R.string.toastMessage_AssociateSensorSuccess),
                                Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getContext(), getResources().getString(R.string.toastMessage_AssociateSensorFail),
                                Toast.LENGTH_LONG).show();
                    }
                }
                newDevRoom.addDevice(newActuator);
                if(HouseManager.getInstance().addDevice(newActuator)){
                    Toast.makeText(this.getContext(), getResources().getString(R.string.toastMessage_AddDeviceSuccess),
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this.getContext(), getResources().getString(R.string.toastMessage_AddDeviceFail),
                            Toast.LENGTH_LONG).show();
                }
            }
            ((MainActivity) this.getContext()).getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new DevicesFragment()).commit();
            HouseManager.setBundle(null);
            MainActivity.clearFragmentsVisitedList();
            return;
        }
        Toast.makeText(this.getContext(), R.string.toastMessage_MissingDevRoom, Toast.LENGTH_LONG).show();
    }

    /**
     * Método que recebe o sensor a ser associado ao dispositivo.
     * @param sensorToAssociate Sensor a ser associado.
     */
    public void setSensorToAssociate(Sensor sensorToAssociate) {
        this.sensorToAssociate = sensorToAssociate;
    }

    /**
     * Método da interface onDestroy, que é chamado quando a classe é destruída.
     * Ele adiciona a classe ao histórico de fragmentos visitados.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(ADD_DEVICES_SELECT_ROOM_FRAGMENT);
    }

    /**
     * Método da interface SaveData, que não tem implementação nesta classe.
     */
    @Override
    public void saveData() {

    }

    /**
     * Método da interface SaveData, que não tem implementação nesta classe.
     */
    @Override
    public void recoverData() {
        try {
            Bundle bundle = HouseManager.getBundle();
            newDevName = bundle.getString(AddDeviceFragment.RESULT_NEW_DEV_NAME);
            newDevChannel = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_CHANNEL);
            newDevType = DeviceType.values()[bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_TYPE)];
            newDevMode = bundle.getInt(AddDeviceFragment.RESULT_NEW_DEV_MODE);
        }catch (NullPointerException e){
            System.out.println("Erro ao recuperar os dados do novo dispositivo.");
            System.out.println(e.getMessage());
            ((MainActivity) this.getActivity()).setInitialFragment();
        }
    }
}