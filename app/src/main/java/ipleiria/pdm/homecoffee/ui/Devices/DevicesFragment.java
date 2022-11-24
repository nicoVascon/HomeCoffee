package ipleiria.pdm.homecoffee.ui.Devices;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.databinding.FragmentDevicesBinding;
import ipleiria.pdm.homecoffee.databinding.FragmentGalleryBinding;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryViewModel;

public class DevicesFragment extends Fragment {
    private HouseManager gestorContactos;
    private RecyclerView mRecyclerView;
    private RecycleDevicesAdapter dAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerView = getView().findViewById(R.id.recyclerViewDevices);
        dAdapter = new RecycleDevicesAdapter(this.getActivity());
        mRecyclerView.setAdapter(dAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
    }


}