package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.model.Actuator;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Sensor;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;
/**
 * Classe RecycleDevicesAdapter é um adapter personalizado para o RecyclerView que contém itens de dispositivos.
 * Ele extende RecyclerView.Adapter e usa DevicesHolder como seu ViewHolder.
 * Ele possui dois construtores para ser usado tanto para DevicesFragment quanto para RoomFragment.
 *
 */
public class RecycleDevicesAdapter extends RecyclerView.Adapter<RecycleDevicesAdapter.DevicesHolder> {
    private HouseManager houseManager;
    private Context context;
    private LayoutInflater mInflater;
    private DevicesFragment devicesFragment;
    private RoomFragment roomFragment;
    private ArrayList<Device> devices;
    /**
     * Construtor para a classe RecycleDevicesAdapter, ele é usado no DevicesFragment
     * @param context contexto da aplicação
     * @param devicesFragment fragmento onde será usado o RecyclerView
     * @param devices lista de dispositivos que será usada no adapter
     */
    public RecycleDevicesAdapter(Context context, DevicesFragment devicesFragment, ArrayList<Device> devices){
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context=context;
        this.devicesFragment = devicesFragment;
        this.devices=devices;
    }
    /**
     * Construtor para a classe RecycleDevicesAdapter, ele é usado no RoomFragment
     * @param context contexto da aplicação
     * @param roomFragment fragmento onde será usado o RecyclerView
     * @param devices lista de dispositivos que será usada no adapter
     */
    public RecycleDevicesAdapter(Context context,RoomFragment roomFragment, ArrayList<Device> devices){
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context=context;
        this.devices=devices;
        this.roomFragment=roomFragment;
    }
    /**
     * onCreateViewHolder é um método que cria o ViewHolder e infla o layout de item de dispositivo.
     * @param parent o ViewGroup onde o ViewHolder será adicionado.
     * @param viewType o tipo da View.
     * @return um novo DevicesHolder contendo a View infladada.
     */
    public RecycleDevicesAdapter.DevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_device_layout,parent, false);
        return new DevicesHolder(mItemView, this);
    }
    /**
     * A classe DevicesHolder é usada para armazenar as views de cada item de dispositivo.
     */
    public class DevicesHolder extends RecyclerView.ViewHolder {
        public final TextView txtConnectionState;
        public final TextView txtDevName;
        public final TextView txtNumDev;
        public final Switch switchDev;
        public final ImageView imgPhoto;
        public final CardView cardView_dev;
        public final RecycleDevicesAdapter dAdapter;
        /**
         * Construtor da classe DevicesHolder
         * @param itemView view do item de dispositivo
         * @param adapter adaptador que contém esses itens
         */
        public DevicesHolder(@NonNull View itemView,  RecycleDevicesAdapter adapter) {
            super(itemView);

            txtConnectionState = itemView.findViewById(R.id.textViewConnectionState);
            txtDevName = itemView.findViewById(R.id.textViewDeviceName);
            txtNumDev = itemView.findViewById(R.id.textViewDevValue);
            switchDev = itemView.findViewById(R.id.switch_device);
            imgPhoto= itemView.findViewById(R.id.imageViewDevicePhoto);
            cardView_dev = itemView.findViewById(R.id.cardView_dev);
            this.dAdapter = adapter;


            /**
             * Implementa a mudança de estado do Switch para habilitar ou desabilitar o dispositivo e atualiza a interface
             */
            switchDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Device itemDevice = getDevice();

                    itemDevice.setConnectionState(isChecked);

                    if (DevicesFragment.isDevicesEnable()){
                        if(devicesFragment!=null) {
                            devicesFragment.updateDevicesConnectionState();
                        }
                        else {
                            roomFragment.updateDevicesConnectionState();
//                        dAdapter.notifyDataSetChanged();
                        }
                    }
                    txtConnectionState.setText(isChecked ? R.string.txt_connectionStateConnected : R.string.txt_connectionStateDisconected);
                    buttonView.setText(isChecked ? R.string.btn_OnDevices : R.string.btn_OffDevices);
                }
            });
        }

        /**
         * Método que é usado para ir buscar o dispositivo correspondente á posição clicada
         * @return Dispositivo na posição clicada
         */
        public Device getDevice(){
            return houseManager.getDevice(this.getLayoutPosition() - 1);
        }
    }
    /**
     * Vincula os dados de um dispositivo às views do item de dispositivo, ou seja atualiza os dados exibidos pelo item
     * @param holder holder que contém as views do item
     * @param position a posição do dispositivo na lista de dispositivos
     */
    @Override
    public void onBindViewHolder(@NonNull RecycleDevicesAdapter.DevicesHolder holder, int position) {
        if (position == 0){
            String gatewayName = houseManager.getGatewayBLEServerName();
            holder.txtDevName.setText(gatewayName != null && !gatewayName.trim().isEmpty()? gatewayName :
                    context.getResources().getString(R.string.txt_GatewayNotDefined));
            holder.txtConnectionState.setText(getContext().getResources().getString(R.string.txt_DeviceEUICode) +
                    (gatewayName != null && !gatewayName.trim().isEmpty()?
                            houseManager.getGatewayBLEServerDevEuiCode() : ""));
            holder.txtNumDev.setText("");
            holder.switchDev.setVisibility(View.GONE);
            holder.imgPhoto.setImageResource(R.drawable.gateway_icon);
            holder.cardView_dev.setCardBackgroundColor(
                            context.getResources().getColor(android.R.color.holo_red_light));
            holder.itemView.setLongClickable(true);
            holder.itemView.setClickable(true);

            /**
             * Mostra um dialogo de confirmação para desassociar o gateway BLE
             */
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(context.getResources().getString(R.string.txt_AlertDialog_DisassociateGatewayTitle))
                            .setMessage(context.getResources().getString(R.string.txt_AlertDialog_DisassociateGateway) +
                                    "\n\n\"" + HouseManager.getInstance().getGatewayBLEServerName() + "\"")
                            .setPositiveButton(context.getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    HouseManager.getInstance().setGatewayBLEServerName(null);
                                    HouseManager.getInstance().setGatewayBLEServerDevEuiCode(null);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(context.getResources().getString(R.string.txt_no), null)
                            .setIcon(R.drawable.link_icon)
                            .show();
                }
            });
            return;
        }


        Device devCurrent = devices.get(position - 1);
        holder.txtDevName.setText(devCurrent.getName());
        if (!DevicesFragment.isDevicesEnable()){
            devCurrent.setConnectionState(false);
            holder.switchDev.setEnabled(false);
        } else{
            holder.switchDev.setEnabled(true);
        }
        holder.txtConnectionState.setText(devCurrent.isConnectionState() ? R.string.txt_connectionStateConnected : R.string.txt_connectionStateDisconected);
        holder.switchDev.setChecked(devCurrent.isConnectionState());
        holder.switchDev.setVisibility((devCurrent instanceof Actuator)? View.VISIBLE : View.GONE);
        holder.switchDev.setText(devCurrent.isConnectionState() ? R.string.btn_OnDevices : R.string.btn_OffDevices);
        holder.txtNumDev.setText(String.format("%.2f", devCurrent.getValue()) + " %");
        holder.cardView_dev.setCardBackgroundColor(
                (devCurrent instanceof Sensor) ?
                        context.getResources().getColor(R.color.devIconBackground) :
                        context.getResources().getColor(R.color.devTypeSpinnerIconBackground));
        switch (devCurrent.getType()){
            case DIGITAL:
                holder.imgPhoto.setImageResource(R.drawable.digital_icon);
                break;
            case ANALOG:
                holder.imgPhoto.setImageResource(R.drawable.analog_icon);
                break;
            case PRESENCE:
                holder.imgPhoto.setImageResource(R.drawable.presence_icon);
                break;
            case HUMIDITY:
                holder.imgPhoto.setImageResource(R.drawable.humiditysensor);
                break;
            case TEMPERATURE:
                holder.imgPhoto.setImageResource(R.drawable.temperaturesensor);
                break;
            case LUMINOSITY:
                holder.imgPhoto.setImageResource(R.drawable.lightsensor);
                break;
            case ACCELERATION:
                holder.imgPhoto.setImageResource(R.drawable.accelerationsensor);
                break;
            case PRESSURE:
                holder.imgPhoto.setImageResource(R.drawable.preassuresensor);
                break;
        }
        holder.itemView.setLongClickable(true);
        holder.itemView.setClickable(true);
        int itemPosition = position - 1;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, itemPosition);
            }
        });
    }
    /**
     * Método chamado quando um item é clicado
     * @param view a view do item clicado
     * @param position a posição do item clicado na lista
     */
    public void onItemClick(View view, int position){
    }

    /**
     * Retorna o número de itens na lista de dispositivos.
     * Adiciona 1 ao tamanho da lista para contar o item "gateway"
     * @return inteiro representando o número de itens na lista
     */
    @Override
    public int getItemCount() {
        return devices.size() +1;
    }
    /**
     * Retorna o contexto da aplicação
     * @return contexto da aplicação
     */
    public Context getContext() {
        return context;
    }
}
