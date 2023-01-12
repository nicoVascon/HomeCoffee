package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Sensor;

/**
 * Adaptador que representa os itens de dispositivos
 * @param <D> extensão para os Devices
 */
public class RecycleDevicesMiniAdapter<D extends Device> extends RecyclerView.Adapter<RecycleDevicesMiniAdapter.DevicesHolder> {
    /**
     * HouseManager é um objeto que gerencia a casa e seus dispositivos.
     */
    private HouseManager houseManager;
    /**
     * Context é o contexto da aplicação.
     */
    private Context context;
    /**
     * LayoutInflater é um objeto usado para inflar layouts.
     */
    private LayoutInflater mInflater;
    /**
     * ArrayList<D> é uma lista de objetos do tipo D que estende a classe Device.
     */
    private ArrayList<D> itemsList;

    /**
     * Construtor da classe
     * @param context contexto da aplicação
     * @param itemsList lista de items a serem mostrados
     */
    public RecycleDevicesMiniAdapter(Context context, ArrayList<D> itemsList){
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context=context;
        this.itemsList = itemsList;
    }
    /**
     * Metodo que cria o holder do item
     * @param parent ViewGroup parent
     * @param viewType int
     * @return DevicesHolder
     */
    public RecycleDevicesMiniAdapter.DevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_device_mini_layout,parent, false);
        return new DevicesHolder(mItemView, this);
    }

    /**
     *Classe DevicesHolder é uma classe ViewHolder para o RecyclerView que contém informações sobre o dispositivo
     */
    public class DevicesHolder extends RecyclerView.ViewHolder {
        public final TextView txtDevName;
        public final ImageView imgPhoto;
        public final CardView cardView_dev;
        public final RecycleDevicesMiniAdapter dAdapter;

        /**
         * DevicesHolder is a ViewHolder class for the RecyclerView that holds the device information.
         *
         * It holds the TextView that displays the device name, the ImageView that displays the device photo,
         *
         * the CardView that holds the entire device information, and the RecycleDevicesMiniAdapter instance.
         *
         * @param itemView the itemView passed in the constructor
         *
         * @param adapter the RecycleDevicesMiniAdapter instance passed in the constructor
         */
        public DevicesHolder(@NonNull View itemView,  RecycleDevicesMiniAdapter adapter) {
            super(itemView);

            txtDevName = itemView.findViewById(R.id.textViewDeviceName_mini);
            imgPhoto= itemView.findViewById(R.id.imageViewDevicePhoto_mini);
            cardView_dev = itemView.findViewById(R.id.cardView_dev_mini);
            this.dAdapter = adapter;
        }
    }

    /**
     * Metodo chamado pelo RecyclerView para exibir os dados em uma determinada posição
     * @param holder objeto DevicesHolder para atualizar as visualizações com informações do objeto D correspondente
     * @param position posição do objeto D na lista
     */
    @Override
    public void onBindViewHolder(@NonNull RecycleDevicesMiniAdapter.DevicesHolder holder, int position) {
        D devCurrent = itemsList.get(position);
        holder.txtDevName.setText(devCurrent.getName());
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
        holder.itemView.setClickable(true);
        int itemPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, itemPosition);
            }
        });
    }
    /**
     * Retorna o objeto D na posição especificada
     * @param position posição do objeto D na lista
     * @return objeto D na posição especificada
     */
    public D getItem(int position){
        return itemsList.get(position);
    }
    /**
     * Método chamado quando um item é clicado
     * @param view view clicada
     * @param position posição do objeto D na lista
     */
    public void onItemClick(View view, int position){
    }
    /**
     * Retorna o tamanho da lista de objetos D
     * @return tamanho da lista de objetos D
     */
    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    /**
     * Retorna o contexto atual
     * @return o contexto atual
      */
    public Context getContext() {
        return context;
    }
}
