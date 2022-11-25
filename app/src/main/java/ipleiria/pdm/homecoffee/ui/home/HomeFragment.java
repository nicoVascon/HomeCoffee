package ipleiria.pdm.homecoffee.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.databinding.FragmentHomeBinding;
import ipleiria.pdm.homecoffee.ui.Devices.AddDeviceFragment;

public class HomeFragment extends Fragment {

    //private FragmentHomeBinding binding;
    private HouseManager houseManager;
    private RecyclerView mRecyclerView;
    private RecycleRoomsAdapter mAdapter;

    private Button addRoomButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textViewGreeting;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;*/

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        houseManager = HouseManager.getInstance();

        // fbutton = getView().findViewById(R.id.floatingActionButton);
        // fbutton.setImageResource(HouseManager.getInstance().getrImage());
        mRecyclerView = getView().findViewById(R.id.RecyclerViewMain);
        mAdapter = new RecycleRoomsAdapter(this.getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }
}