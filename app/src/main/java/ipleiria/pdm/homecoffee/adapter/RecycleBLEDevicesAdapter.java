package ipleiria.pdm.homecoffee.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;

/**

 Adapter que se encarrega de mostrar a lista de dispositivos BLE encontrados.

 Utiliza o RecyclerView para mostrar os dispositivos em forma de lista.

 Utiliza a classe {@link BLEDevicesHolder} como "container" para cada dispositivo.

 @author Carlos Almeida
 @author Nicolas Vasconcellos
 */
public class RecycleBLEDevicesAdapter extends RecyclerView.Adapter<RecycleBLEDevicesAdapter.BLEDevicesHolder> {
    /**
     * Instância singleton do HouseManager
     */
    private HouseManager houseManager;
    /**
     * Contexto da aplicação
     */
    private Context context;
    /**
     * LayoutInflater utilizado para mostrar o layout do item da lista
     */
    private LayoutInflater mInflater;
    /**
     * Lista de dispositivos BLE encontrados
     */
    private List<BluetoothDevice> devices;

    /**
     * View do item da lista selecionado anteriormente
     */
    private View lastSelectedItemView;

    /**
     * Construtor do adaptador que mostra os dispositivos Bluetooth numa recycle view
     * @param context contexto onde o adaptador será utilizado
     * @param devices lista de dispositivos Bluetooth que serão exibidos pelo adaptador
     */
    public RecycleBLEDevicesAdapter(Context context, List<BluetoothDevice> devices) {
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context = context;
        this.devices = devices;
    }
    /**
     * Cria uma nova visualização (Linha) para o adaptador.
     * @param parent   o grupo de visualizações pai onde a nova visualização será adicionada após a criação.
     * @param viewType tipo da nova visualização. Pode ser utilizado para diferenciar entre vários tipos de visualizações dentro do adaptador.
     * @return nova instância de BLEDevicesHolder
     */
    public RecycleBLEDevicesAdapter.BLEDevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_ble_device_layout, parent, false);
        return new RecycleBLEDevicesAdapter.BLEDevicesHolder(mItemView, this);
    }

    /**
     * Classe interna que representa cada item do RecycleView
     */
    public class BLEDevicesHolder extends RecyclerView.ViewHolder {
        public final TextView textView_BLEDevName;
        public final ImageView imgPhoto;
        final RecycleBLEDevicesAdapter dAdapter;

        /**
         * Construtor da classe que representa cada item
         * @param itemView View onde irá ser colocado o nome do dispositivo BLE
         * @param adapter Adaptador que colocará os dispositivos na recycleView
         */
        public BLEDevicesHolder(@NonNull View itemView, RecycleBLEDevicesAdapter adapter) {
            super(itemView);

            textView_BLEDevName = itemView.findViewById(R.id.textView_BLEDevName);
            imgPhoto = itemView.findViewById(R.id.imageViewBLEDevPhoto);
            this.dAdapter = adapter;
        }
    }

    /**
     * onBindViewHolder é um método que vincula os dados de um dispositivo Bluetooth ao ViewHolder.
     * Ele configura o nome do dispositivo para o TextView e configura uma imagem para o ImageView.
     * Além disso, ele configura um listener de clique para o itemView, onde a cor de fundo do item selecionado é alterada.
     * @param holder O ViewHolder que segura os dados para o item.
     * @param position A posição do item no adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull RecycleBLEDevicesAdapter.BLEDevicesHolder holder, int position) {
        BluetoothDevice BLEDevice = this.devices.get(position);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        holder.textView_BLEDevName.setText(BLEDevice.getName() != null && !BLEDevice.getName().trim().isEmpty()? BLEDevice.getName() :
                context.getResources().getString(R.string.txt_NoBLEDevName));
        holder.imgPhoto.setImageResource(R.drawable.ble_icon);
        //holder.imgPhoto.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        int itemPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            /**
             * onClick é um método de retorno de chamada que é chamado quando um item da recycleView é clicado.
             * Ele muda a cor de fundo do item anterior selecionado e define a cor de fundo do item atualmente selecionado.
             * Também chama o método onItemClick com a view e a posição do item clicado.
             *
             * @param v a view que foi clicada
             */
            @Override
            public void onClick(View v) {
                if (lastSelectedItemView != null){
                    CardView lastCardView = lastSelectedItemView.findViewById(R.id.cardView_BLEDev);
                    lastCardView.setCardBackgroundColor(context.getResources().getColor(R.color.iconBackgoundRooms));
                }
                lastSelectedItemView = v;
                CardView cardView = v.findViewById(R.id.cardView_BLEDev);
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.devIconBackground));
                onItemClick(v, itemPosition);
            }
        });
    }
    /**
     * onItemClick é um método de retorno de chamada que é chamado quando um item no RecyclerView é clicado.
     *
     * @param view a view que foi clicada
     * @param position a posição da view no adapter
     */
    public void onItemClick(View view, int position){
    }
    /**
     * getItemCount retorna o número de itens no adapter.
     *
     * @return o número de itens no adapter.
     */
    @Override
    public int getItemCount() {
        return devices.size();
    }
    /**
     * Retorna o contexto do adapter.
     *
     * @return o contexto do adapter.
     */
    public Context getContext() {
        return context;
    }
}
