package ipleiria.pdm.homecoffee.ui.home;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
 * Classe AddRoomFragment é uma extensão da classe Fragment, responsável por adicionar um novo quarto à base de dados do Firebase.
 * Ela contém uma instância do FirebaseFirestore, responsável por se comunicar com a base de dados do Firebase.
 * Também contém um Spinner, responsável por exibir as opções de tipo de quarto disponíveis.
 */
public class AddRoomFragment extends Fragment {

    /**
     * Instância do FirebaseFirestore para acesso ao banco de dados.
     */
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Spinner para seleção do tipo do quarto.
     */
    private Spinner roomTypeSpinner;

    /**
     * Método que infla o layout do fragmento e retorna a view criada.
     * @param inflater é o objeto que irá carregar o layout.
     * @param container é o container onde o fragmento será adicionado.
     * @param savedInstanceState é o estado anterior do fragmento, caso exista.
     * @return view criada a partir do layout inflado.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_room, container, false);
    }

    /**
     * Método chamado quando o fragmento é iniciado.
     * Inicializa as configurações da tela, como o título da toolbar e o adapter do Spinner.
     */
    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_addRomTitle));

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

    /**
     * O método addRoom() cria um novo objeto do tipo Room, com o nome e tipo fornecido pelo usuário
     * e adiciona-o ao HouseManager e à base de dados Firebase Firestore.
     */
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


        if (HouseManager.getInstance().getRooms().contains(newRoom)) {
            Toast.makeText(this.getActivity(), "Room already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        Context context = this.getActivity();

        final ProgressDialog loading = ProgressDialog.show(this.getActivity(), "Uploading...", "Please wait...", false, false);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_URL, response -> {
            loading.dismiss();
            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();

            HouseManager.getInstance().addRoom(newRoom);
            ((MainActivity) context).setInitialFragment();

        }, error ->{
            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

        }
        )


        {
                @Override
                protected Map<String, String> getParams () {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(KEY_ACTION, "insert");
                    //Onde esta 1 era userId
                    params.put(KEY_ID, "userId");
                    params.put(KEY_NAME, nome);
                    params.put(KEY_IMAGE, String.valueOf(type));

                    return params;
                }
        };

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());

        requestQueue.add(stringRequest);

        //Getting current user's email
        String userMail=HouseManager.getInstance().getUser().getEmail();



        //Saving room on Firebase's Firestore
        // Create a new room
        Map<String, Object> room = new HashMap<>();
        room.put("User_Email", userMail);
        room.put("Room_Name", nome);
        room.put("Room_Type", type);

        // Add a new document with a generated ID
        db.collection("rooms")
                .add(room)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


        //HouseManager.getInstance().adicionarContacto(newRoom);
        ((MainActivity) getActivity()).setInitialFragment();
    }

    /**
     * Este método é chamado quando o fragmento é destruido e diz á main activity que este fragmento foi visitado
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(FragmentsEnum.ADD_ROOM_FRAGMENT);
    }
}
