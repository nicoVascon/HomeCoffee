package ipleiria.pdm.homecoffee.ui.home;

import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_ACTION;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_ID;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_IMAGE;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_NAME;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.USER_URL;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
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
            Toast.makeText(context, response, Toast.LENGTH_LONG).show();

            HouseManager.getInstance().addRoom(newRoom);
            ((MainActivity) context).setInitialFragment();

        }, error ->
            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show())
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


        //HouseManager.getInstance().adicionarContacto(newRoom);
        ((MainActivity) getActivity()).setInitialFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.addFragmentViseted(FragmentsEnum.ADD_ROOM_FRAGMENT);
    }
}
