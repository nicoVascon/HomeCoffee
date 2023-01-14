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

public class AddRoomFragment extends Fragment {

    FirebaseFirestore db;

    private Spinner roomTypeSpinner;
    private HouseManager houseManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_room, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.toolbar_addRomTitle));
        houseManager=HouseManager.getInstance();

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

    private void addRoom() {
        EditText et_name = getView().findViewById(R.id.editTextRoomName);
        String nome = et_name.getText().toString();

        Spinner type_spinner = getView().findViewById(R.id.roomType_spinner);
        RoomType type = RoomType.valueOf(type_spinner.getSelectedItem().toString());

        if (nome.trim().isEmpty()) {
            Toast.makeText(this.getActivity(), R.string.txt_addRoomName,Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<Room> rooms= houseManager.getRooms();

        for(Room room : rooms){
            if(room.getRoom_Name().equalsIgnoreCase(nome)){
                Toast.makeText(this.getActivity(), R.string.txt_NameProtection,Toast.LENGTH_LONG).show();
                return;
            }
        }


        Room newRoom = new Room(nome, type);

//
//
//        //Getting current user's email
//        String userMail = HouseManager.getInstance().getUser().getEmail();
//
//
//        //Saving room on Firebase's Firestore
//        // Create a new room
//        Map<String, Object> room = new HashMap<>();
//        //room.put("User_Email", userMail);
//        room.put("Room_Name", nome);
//        room.put("Room_Type", type);
//        room.put("Sensors", newRoom.getSensors());
//        room.put("Actuators", newRoom.getSensors());
//
//
//        CollectionReference usersRef = db.collection("users");
        HouseManager.getInstance().addRoom(newRoom);
//        HouseManager.getInstance().getUser().getRoomsRef().document(nome).set(room);

//        Query query = usersRef.whereEqualTo("User_Email", userMail);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        // Found the user
//                        String userId = document.getId();
//                        // add the post collection
//                        CollectionReference roomsRef = db.collection("users").document(userId).collection("rooms");
//                        DocumentReference roomRef = roomsRef.document(nome);
//                        // you can now add document to the posts collection
////                        roomsRef.add(room);
//                        roomRef.set(room);
//                    }
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });

        ((MainActivity) getActivity()).setInitialFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(FragmentsEnum.ADD_ROOM_FRAGMENT);
    }
}
