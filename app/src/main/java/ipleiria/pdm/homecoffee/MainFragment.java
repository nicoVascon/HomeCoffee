package ipleiria.pdm.homecoffee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;

public class MainFragment extends Fragment {

    private HouseManager gestorContactos;
    private RecyclerView mRecyclerView;
    private RecycleRoomsAdapter mAdapter;
    private FloatingActionButton fbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
       // fbutton = getView().findViewById(R.id.floatingActionButton);
       // fbutton.setImageResource(HouseManager.getInstance().getrImage());
        mRecyclerView = getView().findViewById(R.id.RecyclerViewMain);
        mAdapter = new RecycleRoomsAdapter(this.getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));
    }

}