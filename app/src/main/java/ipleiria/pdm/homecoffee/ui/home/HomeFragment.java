package ipleiria.pdm.homecoffee.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.components.LoadingDialog;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.ui.rooms.AddRoomFragment;
import ipleiria.pdm.homecoffee.ui.rooms.RoomFragment;

public class HomeFragment extends Fragment  {
    public static final String RESULT_ROOM_POSITION = "RESULT_ROOM_POSITION";
    //private FragmentHomeBinding binding;
    private HouseManager houseManager;
    private RecyclerView mRecyclerView;
    private RecycleRoomsAdapter mAdapter;
    private TextView textViewTemp;

    private Button addRoomButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = this.getActivity();
        houseManager = HouseManager.getInstance();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.app_name));

        mRecyclerView = getView().findViewById(R.id.RecyclerViewMain);
        mAdapter = new RecycleRoomsAdapter(this.getActivity()){

            @Override
            public void onItemClick(View v, int position){
                super.onItemClick(v,position);
                Bundle bundle = HouseManager.getBundle();
                if (bundle == null){
                    bundle = new Bundle();
                    HouseManager.setBundle(bundle);
                }
                bundle.putInt(RESULT_ROOM_POSITION, position);
                //((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomFragment()).commit();
                RoomFragment roomFragment = new RoomFragment();
                roomFragment.setArguments(bundle);
                ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, roomFragment).commit();

            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));

        addRoomButton = getView().findViewById(R.id.buttonAddRoom);
        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddRoomFragment()).commit();
            }
        });

        getSensorData();

        LoadingDialog loadingDialog = new LoadingDialog(this.getActivity());
        try {
            loadingDialog.startLoadingDialog();
        }catch (Exception e){
            // The dialog may be already showed
        }
        loadingDialog.setMainText("Getting rooms...");
        houseManager.getUserRooms(mAdapter,loadingDialog);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.HOME_FRAGMENT);
        //binding = null;
    }

    public void getSensorData(){

        Context context = this.getActivity();
        // Create a RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);



        // Set the TTN application ID and access key
        String appId = "teste-rs2022";
        String accessKey = "NNSXS.QCAXYRDU5ANJFWYLS32EKNDYYAHZJLWYO7OT4VI.7GDTOTVXMQVYR2SI4WNO22QRAENSGL3WIDC75I2NRYVRDUTPO4CQ";

        // Set the URL for the TTN HTTP API endpoint
        //String url = "https://" + appId + ".data.thethingsnetwork.org/api/v2/query";
        String limit_msgs="30";
        String url = "https://eu1.cloud.thethings.network/api/v3/as/applications/teste-rs2022/packages/storage/uplink_message?order=-received_at&limit=30&field_mask=up.uplink_message.decoded_payload";

/*
        // Set the parameters for the API request
        Map<String, String> params = new HashMap<>();
        params.put("last", "1");
        params.put("limit", "30");
*/
        // Set the headers for the API request
        Map<String, String> headers = new HashMap<>();
        //headers.put("Authorization", "Key " + accessKey);
        headers.put("Authorization", "Bearer " + accessKey);
        headers.put("Accept", "text/event-stream" + accessKey);



        // Make the request to the TTN HTTP API

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response.toString());



                            /*Map result = ((Map)response.get("result"));
                            //Map up_msg = (result.get("uplink_message"));
                            //Map dec_payl = ((Map)up_msg.get("decoded_payload"));


                            // iterating address Map
                            Iterator<Map.Entry> itr1 = result.entrySet().iterator();
                            while (itr1.hasNext()) {
                                Map.Entry pair = itr1.next();
                                System.out.println(pair.getKey() + " : " + pair.getValue());

                            }
*/

                            JSONObject up_msg = response.getJSONObject("result");
                            JSONObject dec_payl = up_msg.getJSONObject("uplink_message");
                            JSONObject values = dec_payl.getJSONObject("decoded_payload");
                            System.out.println(values);

                            //Toast.makeText(context, values.toString(), Toast.LENGTH_LONG).show();
                            //Toast.makeText(context, "Ola eu recebi uma resposta", Toast.LENGTH_SHORT).show();
                            //System.out.println("XXXXXXXXXXXXXXXXX\n\n\n\n\n\n\n\n\n\n\nXXXXXXXXXXXXXX");
                            //System.out.println(value);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error here
                        //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
                        System.out.println(error.getMessage());
                        System.out.println(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

        };
/*
        //GET A JSON ARRAY
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //try {
                            System.out.println("Cheguei ao array");
                            System.out.println(response);
                            /*for (int i = 0; i < response.length(); i++) {
                                JSONObject result = response.getJSONObject(i);
                                JSONObject uplinkMessage = result.getJSONObject("uplink_message");
                                JSONObject decoded_payload = uplinkMessage.getJSONObject("decoded_payload");
                                System.out.println(decoded_payload);
                                textViewTemp.setText(decoded_payload.toString());
                            }
                            */

                       // } catch (JSONException e) {
                        //    e.printStackTrace();
                       // }
                   /* }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error here
                        //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
                        System.out.println(error.getMessage());
                        System.out.println(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

        };
        */



        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);

    }
}