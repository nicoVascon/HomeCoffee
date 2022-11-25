package ipleiria.pdm.homecoffee.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ipleiria.pdm.homecoffee.DeviceType;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.Room;
import ipleiria.pdm.homecoffee.RoomType;

public class AddHomeFragment extends Fragment {

    private Spinner roomTypeSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_room, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        roomTypeSpinner = getView().findViewById(R.id.roomType_spinner);

        ArrayList<String> arrayList = new ArrayList<>();
        for (DeviceType deviceType : DeviceType.values()){
            arrayList.add(deviceType.toString());
        }

        ArrayAdapter<RoomType> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, RoomType.values());
        roomTypeSpinner.setAdapter(adapter);


        //Adicionar quartos

        Button btn_add_room = getView().findViewById(R.id.buttonAddRoom_w_values);
        btn_add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoom();
            }
        });

        //------------------------

    }

    private void addRoom() {
        EditText et_name = getView().findViewById(R.id.editTextRoomName);
        String nome = et_name.getText().toString();

        Spinner type_spinner = getView().findViewById(R.id.roomType_spinner);
        RoomType type = RoomType.valueOf(type_spinner.getSelectedItem().toString());

        if (nome.trim().isEmpty() ) {
            Toast.makeText(this.getActivity(), "Insert a name",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Room newRoom = new Room(nome,type);
        HouseManager.getInstance().adicionarContacto(newRoom);
        ((MainActivity) getActivity()).setInitialFragment();


    }
}
