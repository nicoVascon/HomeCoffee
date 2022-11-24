package ipleiria.pdm.homecoffee.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HouseManager gestorContactos;
    private RecyclerView mRecyclerView;
    private RecycleRoomsAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textViewGreeting;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}