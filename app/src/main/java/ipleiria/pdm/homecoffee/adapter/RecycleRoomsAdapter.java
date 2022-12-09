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

public class RecycleRoomsAdapter extends RecyclerView.Adapter<RecycleRoomsAdapter.RoomsHolder>{
    private HouseManager gestorContactos;
    private Context context;
    private LayoutInflater mInflater;

    private View lastSelectedItemView;

    public RecycleRoomsAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.gestorContactos = HouseManager.getInstance();
        this.context=context;
    }


    public RoomsHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View mItemView = mInflater.inflate(R.layout.item_layout,parent, false);
        return new RoomsHolder(mItemView, this);
    }
    public class RoomsHolder extends RecyclerView.ViewHolder {
        public final TextView txtName;
        public final ImageView imgPhoto;
        final RecycleRoomsAdapter mAdapter;

        public RoomsHolder(@NonNull View itemView,  RecycleRoomsAdapter adapter) {
            super(itemView);

            txtName = itemView.findViewById(R.id.textViewItemName);
            imgPhoto= itemView.findViewById(R.id.imageViewItemPhoto);
            this.mAdapter = adapter;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsHolder holder, int position) {
        Room mCurrent = gestorContactos.getRooms().get(position);
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

    public void onItemClick(View v, int position){
        if (lastSelectedItemView != null){
            CardView lastCardView = lastSelectedItemView.findViewById(R.id.cardViewRoomItem);
            lastCardView.setCardBackgroundColor(context.getResources().getColor(R.color.roomIconBackground));
        }
        lastSelectedItemView = v;
        CardView cardView = v.findViewById(R.id.cardViewRoomItem);
        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.devIconBackground));
    }

    @Override
    public int getItemCount() {
        return gestorContactos.getRooms().size();
    }

    public Context getContext() {
        return context;
    }
}
