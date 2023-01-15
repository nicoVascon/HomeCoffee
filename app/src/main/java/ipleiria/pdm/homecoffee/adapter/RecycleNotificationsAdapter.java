package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Notification;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
/**
 * Classe adaptador para exibir notificações em um RecyclerView
 */
public class RecycleNotificationsAdapter extends RecyclerView.Adapter<RecycleNotificationsAdapter.NotificationsHolder> {
    /**
     * Instância da classe HouseManager, que gerencia as informações da casa.
     */
    private HouseManager houseManager;
    /**
     * Contexto da aplicação.
     */
    private Context context;
    /**
     * Instância da classe LayoutInflater, que é utilizada para inflar layouts.
     */
    private LayoutInflater mInflater;
    /**
     * Dispositivo selecionado para exibição das notificações.
     */
    private Device selectedDevice;


    /**
     * Construtor
     * @param context contexto atual
     */
    public RecycleNotificationsAdapter(Context context, Device selectedDevice){
        mInflater = LayoutInflater.from(context);
        this.context=context;

        this.selectedDevice = selectedDevice;
    }
    /**
     * Cria uma nova visualização (ou holder) para uma notificação
     * @param parent ViewGroup pai da visualização criada
     * @param viewType tipo de visualização a ser criada
     */
    public RecycleNotificationsAdapter.NotificationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_notification_layout,parent, false);
        return new NotificationsHolder(mItemView, this);
    }
    /**
     * Classe interna que representa o holder de uma notificação
     */
    public class NotificationsHolder extends RecyclerView.ViewHolder {
        public final TextView txtNotificationDate;
        public final TextView txtNotificationDescription;
        public final ImageView imgPhoto;
        public final Button deleteButton;
        final RecycleNotificationsAdapter dAdapter;
        /**
         * Construtor
         * @param itemView visualização da notificação
         * @param adapter adaptador atual
         */
        public NotificationsHolder(@NonNull View itemView, RecycleNotificationsAdapter adapter) {
            super(itemView);

            txtNotificationDate = itemView.findViewById(R.id.textViewNotificationDate);
            txtNotificationDescription = itemView.findViewById(R.id.textViewNotificationDescription);
            imgPhoto= itemView.findViewById(R.id.imageViewNotificationPhoto);
            deleteButton = itemView.findViewById(R.id.btn_deleteNotification);
            this.dAdapter = adapter;
        }
    }

    /**
     * Este método é chamado quando um ViewHolder é vinculado à sua posição. Ele atualiza os campos de texto e imagem do ViewHolder com dados da notificação na posição especificada. Ele também adiciona um evento de clique para o botão "excluir" que exclui a notificação na posição especificada.
     * @param holder O ViewHolder que está sendo vinculado.
     * @param position A posição da notificação que deve ser exibida no ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull RecycleNotificationsAdapter.NotificationsHolder holder, int position) {
        Notification currentNotification = this.selectedDevice.getNotification(position);

        holder.txtNotificationDate.setText(currentNotification.getDateFormatted());
        holder.txtNotificationDescription.setText(currentNotification.getDescription());
        holder.imgPhoto.setImageResource(R.drawable.notification_icon);
        holder.imgPhoto.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        int notificationPosition = position;
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(notificationPosition);
                notifyDataSetChanged();
            }
        });

    }

     /**
     * Este método é chamado quando o botão "excluir" é clicado. Ele remove a notificação na posição especificada.
     * @param position A posição da notificação que deve ser excluída.
     */

    public void onDeleteClick(int position){
        selectedDevice.removeNotification(position);
    }
    /**
     * Este método retorna o número total de notificações do dispositivo selecionado.
     * @return Número total de notificações do dispositivo selecionado.
     */
    @Override
    public int getItemCount() {
        return selectedDevice.getNumNotifications();
    }
    /**
     * Este método retorna o contexto atual.
     * @return O contexto atual.
     */
    public Context getContext() {
        return context;
    }
}
