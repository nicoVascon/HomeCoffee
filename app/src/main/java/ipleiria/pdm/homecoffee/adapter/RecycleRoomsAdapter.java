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
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.Room;

public class RecycleRoomsAdapter extends RecyclerView.Adapter<RecycleRoomsAdapter.RoomsHolder>{
    private HouseManager gestorContactos;
    private Context context;
    private LayoutInflater mInflater;


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
       // public final TextView txtNumber;
        public final ImageView imgPhoto;
        final RecycleRoomsAdapter mAdapter;


        public RoomsHolder(@NonNull View itemView,  RecycleRoomsAdapter adapter) {
            super(itemView);

            txtName = itemView.findViewById(R.id.textViewItemName);
            //txtNumber = itemView.findViewById(R.id.textViewItemNum);
            imgPhoto= itemView.findViewById(R.id.imageViewItemPhoto);
            this.mAdapter = adapter;



        }





    }

    @Override
    public void onBindViewHolder(@NonNull RoomsHolder holder, int position) {
        Room mCurrent = gestorContactos.getRooms().get(position);
        holder.txtName.setText(mCurrent.getNome());
        //holder.txtNumber.setText(Integer.toString(mCurrent.getNumero()));
       /* if (mCurrent.getPathPhoto().trim().isEmpty()) {
            holder.imgPhoto.setImageResource(R.drawable.ic_bedroom_default);
        } else {
            try {
                File f=new File(context.getFilesDir() + "/" +
                        mCurrent.getPathPhoto());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                holder.imgPhoto.setImageBitmap(b);
            } catch (Exception e) {
                holder.imgPhoto.setImageResource(R.drawable.ic_bedroom_default);
            }
        }*/
        /*if (mCurrent.getPathPhoto().trim().isEmpty()) {
            holder.imgPhoto.setImageResource(R.drawable.ic_bedroom_default);
        } else {
        */


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
    }
    @Override
    public int getItemCount() {
        return gestorContactos.getRooms().size();
    }


    public Context getContext() {
        return context;
    }
}
