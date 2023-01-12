package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Room;
/**
 * Classe adapter para RecyclerView para exibir quartos em uma lista. Utiliza o singleton HouseManager para obter informações sobre quartos.
 */
public class RecycleRoomsAdapter extends RecyclerView.Adapter<RecycleRoomsAdapter.RoomsHolder>{
    private HouseManager houseManager;
    private Context context;
    private LayoutInflater mInflater;

    private View lastSelectedItemView;
    /**
     * Construtor. Inicializa as variáveis de instância.
     * @param context Contexto atual.
     */
    public RecycleRoomsAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.houseManager = HouseManager.getInstance();
        this.context=context;
    }

    /**
     * Este método é chamado quando o RecyclerView precisa de um novo ViewHolder para exibir um item.
     * @param parent O grupo pai que contém o RecyclerView.
     * @param viewType O tipo de visualização.
     * @return Um novo RoomsHolder contendo a visualização do item.
     */
    public RoomsHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_layout,parent, false);
        return new RoomsHolder(mItemView, this);
    }
    /**
     * Classe ViewHolder que contém as visualizações do item.
     */
    public class RoomsHolder extends RecyclerView.ViewHolder {
        public final TextView txtName;
        public final ImageView imgPhoto;
        final RecycleRoomsAdapter mAdapter;
        /**
         * Construtor. Inicializa as visualizações do item.
         * @param itemView A visualização do item.
         * @param adapter O adapter que contém este ViewHolder.
         */
        public RoomsHolder(@NonNull View itemView,  RecycleRoomsAdapter adapter) {
            super(itemView);

            txtName = itemView.findViewById(R.id.textViewItemName);
            imgPhoto= itemView.findViewById(R.id.imageViewItemPhoto);
            this.mAdapter = adapter;
        }
    }
    /**
     * Este método é chamado pelo RecyclerView para exibir os dados em uma posição específica. Atualiza as visualizações do ViewHolder com os dados do quarto na posição especificada. Adiciona um listener de clique para alterar a cor de fundo do item selecionado.
     * @param holder O ViewHolder que deve ser atualizado.
     * @param position A posição do quarto que deve ser exibida.
     */
    @Override
    public void onBindViewHolder(@NonNull RoomsHolder holder, int position) {
        Room mCurrent = houseManager.getRooms().get(position);
        holder.txtName.setText(mCurrent.getNome());
        switch (mCurrent.getType()) {
            case BEDROOM:
                holder.imgPhoto.setImageResource(R.drawable.bedroom_alternative);
                break;
            case KITCHEN:
                holder.imgPhoto.setImageResource(R.drawable.kitchen);
                break;
            case LIVING_ROOM:
                holder.imgPhoto.setImageResource(R.drawable.living_room);
                break;
            case OFFICE:
                holder.imgPhoto.setImageResource(R.drawable.office);
                break;
            case BATHROOM:
                holder.imgPhoto.setImageResource(R.drawable.bathroom);
                break;
        }
        holder.itemView.setLongClickable(true);
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
     * Este método é chamado quando um item é clicado. Ele muda a cor de fundo do item selecionado e guarda a referência ao último item selecionado.
     * @param v A visão do item que foi clicado.
     * @param position A posição do item que foi clicado.
     */
    public void onItemClick(View v, int position){
        if (lastSelectedItemView != null){
            CardView lastCardView = lastSelectedItemView.findViewById(R.id.cardViewRoomItem);
            lastCardView.setCardBackgroundColor(context.getResources().getColor(R.color.iconBackgoundRooms));
        }
        lastSelectedItemView = v;
        CardView cardView = v.findViewById(R.id.cardViewRoomItem);
        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.devIconBackground));
    }
    /**
     * Este método retorna o número total de quartos gerenciado pelo HouseManager.
     * @return O número total de quartos gerenciado pelo HouseManager.
     */
    @Override
    public int getItemCount() {
        return houseManager.getRooms().size();
    }

    /**
     * Este método retorna o contexto atual.
     * @return O contexto atual.
     */
    public Context getContext() {
        return context;
    }
}
