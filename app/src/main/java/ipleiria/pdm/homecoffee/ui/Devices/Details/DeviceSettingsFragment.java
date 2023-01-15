package ipleiria.pdm.homecoffee.ui.Devices.Details;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;

/**
 * Este fragmento é responsável por exibir as configurações de um dispositivo selecionado. Ele contém elementos de interface para mostrar o nome, canal, tipo e sala do dispositivo selecionado.
 * Ele também contém elementos de interface para editar essas informações.
 */
public class DeviceSettingsFragment extends Fragment {
    /**
     * Variável de estado para indicar se o dispositivo está sendo editado ou não.
     */
    public static boolean editingDevice;

    /**
     * Instância do dispositivo selecionado.
     */
    private Device selectedDevice;

    /**
     * Elementos de interface para exibir informações do dispositivo, como nome, canal e tipo.
     */
    private TextView txt_devName;
    /**
     * Elementos de interface para exibir informações da sala do dispositivo.
     */
    private TextView txt_devChannel;
    private TextView txt_devTypeName;
    private ImageView imageView_devType;

    private TextView txt_devRoomName;
    private ImageView imageView_devRoom;

    /**
     * Este método é chamado quando o fragmento é criado. Ele inicializa as variáveis e configurações necessárias.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Este método é chamado quando a visualização do fragmento é criada.
     * Ele infla o layout do fragmento, que contém elementos de interface para mostrar e editar as configurações do dispositivo selecionado.
     * @param inflater O objeto LayoutInflater que pode ser usado para inflar o layout do fragmento
     * @param container O contêiner que contém o fragmento.
     * @param savedInstanceState Um objeto Bundle que pode conter informações sobre o estado anterior do fragmento.
     * @return o layout a ser mostrado
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_settings, container, false);
    }

    /**
     * Este método é chamado quando o fragmento é iniciado. Ele configura a exibição das configurações do dispositivo selecionado, incluindo seu nome, canal, tipo e sala.
     * Ele também configura os botões de edição e exclusão do dispositivo.
     */
    @Override
    public void onStart() {
        super.onStart();

//        int devPosition;
//        if(HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
//            int roomPosition = HouseManager.getBundle().getInt(HomeFragment.RESULT_ROOM_POSITION);
//            Room room = HouseManager.getInstance().getRoom(roomPosition);
//            devPosition = HouseManager.getBundle().getInt(RoomFragment.RESULT_DEV_POSITION);
//            selectedDevice = room.getDevices().get(devPosition);
//        }else{
//            devPosition = HouseManager.getBundle().getInt(DevicesFragment.RESULT_DEV_POSITION);
//            selectedDevice = HouseManager.getInstance().getDevice(devPosition);
//        }

        this.selectedDevice = DeviceDetailsFragment.getSelectedDevice();

        txt_devName = getView().findViewById(R.id.textView_DeviceName_SettingsFragment);
        txt_devChannel = getView().findViewById(R.id.textView_DeviceChannel_SettingsFragment);
        Button btn_editDev = getView().findViewById(R.id.btn_editDev);
        Button btn_deleteDev = getView().findViewById(R.id.btn_deleteDev);
        Context context = this.getContext();
        btn_editDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editingDevice = true;
                ((MainActivity) context).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new AddDeviceFragment()).commit();
            }
        });
        btn_deleteDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.txt_AlertDialog_deleteTitle))
                        .setMessage(getResources().getString(R.string.txt_AlertDialog_deleteDev))

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HouseManager.getInstance().removeDevice(selectedDevice);
                                MainActivity.clearFragmentsVisitedList();
                                DeviceDetailsFragment.addAsVisitedFragment = false;
                                Bundle bundle = HouseManager.getBundle();
                                if(bundle != null){
                                    if(bundle.containsKey(DevicesFragment.RESULT_DEV_POSITION)){
                                        HouseManager.getBundle().remove(DevicesFragment.RESULT_DEV_POSITION);
                                    }else if(bundle.containsKey(RoomFragment.RESULT_DEV_POSITION)){
                                        HouseManager.getBundle().remove(RoomFragment.RESULT_DEV_POSITION);
                                    }
                                }

                                ((MainActivity) context).getSupportFragmentManager().beginTransaction().
                                        replace(R.id.fragment_container, new DevicesFragment()).commit();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(getResources().getString(R.string.txt_no), null)
                        .setIcon(R.drawable.delete_icon)
                        .show();
            }
        });

        View deviceTypeLayout = getView().findViewById(R.id.DeviceTypeLayout);
        txt_devTypeName = deviceTypeLayout.findViewById(R.id.textViewDeviceName_spinner);
        imageView_devType = deviceTypeLayout.findViewById(R.id.imageViewDeviceTypePhoto_spinner);

        View deviceRoomLayout = getView().findViewById(R.id.DeviceRoomLayout);
        txt_devRoomName = deviceRoomLayout.findViewById(R.id.textViewItemName);
        imageView_devRoom = deviceRoomLayout.findViewById(R.id.imageViewItemPhoto);

        initDevTypeLayout();
        initRoomLayout();
    }

    /**
     * Método responsável por inicializar o layout de tipo de dispositivo.
     *
     * Ele atualiza o nome e canal do dispositivo selecionado, além de definir a imagem de acordo com o tipo de dispositivo.
     */
    public void initDevTypeLayout(){
        txt_devName.setText(selectedDevice.getName());
        txt_devChannel.setText(String.valueOf(selectedDevice.getChannel()));

        switch (selectedDevice.getType()){
            case DIGITAL:
                imageView_devType.setImageResource(R.drawable.digital_icon);
                break;
            case ANALOG:
                imageView_devType.setImageResource(R.drawable.analog_icon);
                break;
            case PRESENCE:
                imageView_devType.setImageResource(R.drawable.presence_icon);
                break;
            case HUMIDITY:
                imageView_devType.setImageResource(R.drawable.humiditysensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Humidity));
                break;
            case TEMPERATURE:
                imageView_devType.setImageResource(R.drawable.temperaturesensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Temperature));
                break;
            case LUMINOSITY:
                imageView_devType.setImageResource(R.drawable.lightsensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Light));
                break;
            case ACCELERATION:
                imageView_devType.setImageResource(R.drawable.accelerationsensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Acceleration));
                break;
            case PRESSURE:
                imageView_devType.setImageResource(R.drawable.preassuresensor);
                txt_devTypeName.setText(getContext().getResources().getString(R.string.deviceTypeName_Pressure));
                break;
        }
    }

    /**
     * Este método atribui a imagem correspondente ao tipo de quarto do device que a chamou
     */
    public void initRoomLayout(){
        Room devRoom = HouseManager.getInstance().searchRoomByDevice(selectedDevice);
        if(devRoom == null){
            return;
        }
        txt_devRoomName.setText(devRoom.getRoom_Name());
        switch (devRoom.getRoom_Type()) {
            case BEDROOM:
                imageView_devRoom.setImageResource(R.drawable.bedroom_alternative);
                break;
            case KITCHEN:
                imageView_devRoom.setImageResource(R.drawable.kitchen);
                break;
            case LIVING_ROOM:
                imageView_devRoom.setImageResource(R.drawable.living_room);
                break;
            case OFFICE:
                imageView_devRoom.setImageResource(R.drawable.office);
                break;
            case BATHROOM:
                imageView_devRoom.setImageResource(R.drawable.bathroom);
                break;
        }
    }
}