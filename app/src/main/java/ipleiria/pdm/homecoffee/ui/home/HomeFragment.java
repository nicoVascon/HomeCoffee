package ipleiria.pdm.homecoffee.ui.home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

/**
 * Classe responsável pelo fragmento da tela inicial da aplicação.
 */
public class HomeFragment extends Fragment  {
    /**
     * Chave para passar a posição do quarto selecionado na lista para outras telas.
     */
    public static final String RESULT_ROOM_POSITION = "RESULT_ROOM_POSITION";
    //private FragmentHomeBinding binding;
    /**
     * Variável para exibir a lista de quartos.
     */
    private RecyclerView mRecyclerView;
    /**
     * Variável para controlar a exibição dos itens na lista de quartos.
     */
    private RecycleRoomsAdapter mAdapter;
    /**
     * Variável para exibir a temperatura atual.
     */
    private TextView textViewTemp;
    /**
     * Variável para controlar o botão de adicionar quarto.
     */
    private FloatingActionButton addRoomButton;
    private FloatingActionButton fbButtonRemove;
    private FloatingActionButton fbButtonEdit;

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;
    private Boolean btnEditClicked;

    /**
     * Método chamado para mostrar o layout do fragmento home
     *
     * @param inflater utilizado para inflar o layout do fragmento.
     * @param container container onde o fragmento será adicionado.
     * @param savedInstanceState estado salvo da instância anterior.
     * @return view do fragmento de Home.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Método chamado quando o fragmento começa.
     * Inicializa a instância de HouseManager, define o fragmento atual e o título da toolbar como o nome da aplicação.
     * Inicializa o RecyclerView, o adapter e o layout manager.
     * Inicializa o botão de adicionar sala e configura o listener para abrir o fragmento de adicionar salas
     * Chama o método getSensorData.
     */
    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.app_name));

        mRecyclerView = getView().findViewById(R.id.RecyclerViewMain);
        btnEditClicked= Boolean.FALSE;

        rotateOpen = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getContext(), R.anim.to_bottom_anim);


        mAdapter = new RecycleRoomsAdapter(this.getActivity()){
            @Override
            public void onItemClick(View v, int position) {

                if (HouseManager.getInstance().isRoomRemove()) {
                    Room room_remove=HouseManager.getInstance().getRoom(position);
                    HouseManager.getInstance().removeRoom(room_remove);
                    mAdapter.notifyDataSetChanged();
                    HouseManager.getInstance().setRoomRemove(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        HouseManager.getInstance().setColor_back_rooms(getContext().getColor(R.color.iconBackgoundRooms));
                    }
                    mAdapter.notifyDataSetChanged();

                    Toast.makeText(getContext(),R.string.RoomEliminated,Toast.LENGTH_SHORT).show();

                } else {
                    super.onItemClick(v, position);
                    Bundle bundle = HouseManager.getBundle();
                    if (bundle == null) {
                        bundle = new Bundle();
                        HouseManager.setBundle(bundle);
                    }
                    bundle.putInt(RESULT_ROOM_POSITION, position);
                    RoomFragment roomFragment = new RoomFragment();
                    roomFragment.setArguments(bundle);
                    ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, roomFragment).commit();
                }
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));


        fbButtonEdit = getView().findViewById(R.id.floatingButtonEdit);
        fbButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(btnEditClicked);
                setAnimation(btnEditClicked);
                setClickable(btnEditClicked);
                btnEditClicked=!btnEditClicked;
            }
        });


        addRoomButton = getView().findViewById(R.id.floatingButtonAdd);
        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddRoomFragment()).commit();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            HouseManager.getInstance().setColor_back_rooms(getContext().getColor(R.color.iconBackgoundRooms));
        }

        fbButtonRemove = getView().findViewById(R.id.floatingButtonRemove);
        fbButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (HouseManager.getInstance().isRoomRemove()) {
                    HouseManager.getInstance().setRoomRemove(false);
                    Toast.makeText(getContext(), R.string.ELiminateMode, Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        HouseManager.getInstance().setColor_back_rooms(getContext().getColor(R.color.iconBackgoundRooms));
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    HouseManager.getInstance().setRoomRemove(true);
                    Toast.makeText(getContext(), R.string.SelectRoomEliminate, Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        HouseManager.getInstance().setColor_back_rooms(getContext().getColor(R.color.RoomRemoveMode));

                    }
                    mAdapter.notifyDataSetChanged();


                }
            }
        });


        if(!HouseManager.gettingUserRooms){
            HouseManager.gettingUserRooms = true;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                    loadingDialog.startLoadingDialog();
                    loadingDialog.setMainText(getResources().getString(R.string.txt_loadingDialog_GettingRooms));
                    while (!HouseManager.userRoomsRefGotten){
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    HouseManager.getInstance().getUserRooms(mAdapter,loadingDialog);
                }
            });
            thread.start();
        }
    }

    private void setAnimation(Boolean clicked) {
        if(!clicked){
            addRoomButton.startAnimation(fromBottom);
            fbButtonRemove.startAnimation(fromBottom);
            fbButtonEdit.startAnimation(rotateOpen);
        }else{
            addRoomButton.startAnimation(toBottom);
            fbButtonRemove.startAnimation(toBottom);
            fbButtonEdit.startAnimation(rotateClose);
        }
    }

    private void setVisibility(Boolean clicked) {
        if(!clicked){
            addRoomButton.setVisibility(View.VISIBLE);
            fbButtonRemove.setVisibility(View.VISIBLE);
        }else{
            addRoomButton.setVisibility(View.INVISIBLE);
            fbButtonRemove.setVisibility(View.INVISIBLE);
        }
    }

    private void setClickable(Boolean clicked) {
        if(!clicked){
            addRoomButton.setClickable(true);
            fbButtonRemove.setClickable(true);
        }else{
            addRoomButton.setClickable(false);
            fbButtonRemove.setClickable(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(FragmentsEnum.HOME_FRAGMENT);
        //binding = null;
    }

    /**
     * Método que faz uma requisição GET para obter dados de sensores da plataforma The Things Network.
     * Cria uma fila de requisições utilizando a biblioteca Volley.
     * Define o ID da aplicação e a chave de acesso da plataforma The Things Network.
     * Define a URL para o endpoint da API HTTP da plataforma.
     * Define os headers da requisição, incluindo a autorização e o tipo de conteúdo.
     * Faz a requisição e trata a resposta, imprimindo os dados obtidos no console.
     */
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