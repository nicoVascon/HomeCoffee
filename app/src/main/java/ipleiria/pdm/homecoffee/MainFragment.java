package ipleiria.pdm.homecoffee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ipleiria.pdm.homecoffee.adapter.RecycleDevicesAdapter;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;

/**
 * Classe MainFragment representa o fragmento principal da aplicação.
 *
 * É responsável por gerenciar a exibição e interação dos elementos na tela principal.
 */
public class MainFragment extends Fragment {
    /**
     * Atributo que representa o objeto HouseManager, responsável pelo gerenciamento das casas.
     */
    private HouseManager houseManager;
    /**
     * Atributo que representa a RecyclerView, utilizada para exibir as salas cadastradas.
     */
    private RecyclerView mRecyclerView;
    /**
     * Atributo que representa o adapter da RecyclerView, responsável por gerenciar os dados a serem exibidos.
     */
    private RecycleRoomsAdapter mAdapter;
    /**
     * Atributo que representa o adapter da RecyclerView, responsável por gerenciar os dados a serem exibidos.
     */
    private RecycleDevicesAdapter dAdapter;
    /**
     * Atributo que representa o botão flutuante utilizado para adicionar novas salas.
     */
    private FloatingActionButton fbutton;

    /**
     * Método chamado quando a view do fragmento é criada.
     * @param inflater objeto utilizado para inflar a view do fragmento
     * @param container objeto que representa o container onde a view será adicionada
     * @param savedInstanceState objeto que contém o estado anteriormente salvo da aplicação
     * @return view do fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }




}