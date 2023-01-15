package ipleiria.pdm.homecoffee.ui.rooms;

import static android.content.ContentValues.TAG;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_ACTION;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_ID;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_IMAGE;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_NAME;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.USER_URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.Enums.RoomType;

/**
 * Classe que representa o fragmento responsável por adicionar uma nova sala.
 */
public class AddRoomFragment extends Fragment {
    /**
     * Referência da instância do FirebaseFirestore, responsável por acessar e manipular os dados armazenados no banco de dados.
     */
    FirebaseFirestore db;
    /**
     * Referência do Spinner usado para escolher o tipo de sala.
     */
    private Spinner roomTypeSpinner;

    /**
     * Método chamado quando o fragmento é criado. Ele infla o layout do fragmento e o retorna para ser exibido na tela.
     * @param inflater Referência do LayoutInflater usado para inflar o layout do fragmento.
     * @param container Referência da ViewGroup usada como container para o fragmento.
     * @param savedInstanceState Referência do Bundle contendo o estado salvo da instância do fragmento.
     * @return A view do fragmento.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_room, container, false);
    }

    /**
     * Método chamado quando o fragmento é iniciado. Ele configura o título da toolbar, o spinner de tipo de sala e os eventos de clique dos botões.
     */
    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_addRomTitle));

        roomTypeSpinner = getView().findViewById(R.id.roomType_spinner);
        db = FirebaseFirestore.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();
        for (DeviceType deviceType : DeviceType.values()) {
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

    /**
     * Método para adicionar uma nova sala.
     * Ele pega o nome e o tipo de sala inseridos pelo usuário, verifica se já existe uma sala com o mesmo nome e adiciona a sala criada no HouseManager.
     */
    private void addRoom() {
        EditText et_name = getView().findViewById(R.id.editTextRoomName);
        String nome = et_name.getText().toString();

        Spinner type_spinner = getView().findViewById(R.id.roomType_spinner);
        RoomType type = RoomType.valueOf(type_spinner.getSelectedItem().toString());

        if (nome.trim().isEmpty()) {
            Toast.makeText(this.getActivity(), R.string.txt_addRoomName,Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<Room> rooms= HouseManager.getInstance().getRooms();

        for(Room room : rooms){
            if(room.getRoom_Name().equalsIgnoreCase(nome)){
                Toast.makeText(this.getActivity(), R.string.txt_NameProtection,Toast.LENGTH_LONG).show();
                return;
            }
        }


        Room newRoom = new Room(nome, type);
        HouseManager.getInstance().addRoom(newRoom);

        ((MainActivity) getActivity()).setInitialFragment();
    }

    /**
     * Método chamado quando o fragmento é destruído. Ele adiciona o fragmento à lista de fragmentos visitados para uso futuro.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(FragmentsEnum.ADD_ROOM_FRAGMENT);
    }
}
