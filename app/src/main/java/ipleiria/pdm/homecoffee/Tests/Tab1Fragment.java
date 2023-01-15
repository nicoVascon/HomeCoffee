package ipleiria.pdm.homecoffee.Tests;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipleiria.pdm.homecoffee.R;

public class Tab1Fragment extends Fragment {

    /**
     * Este método é chamado quando o fragmento é criado.
     * Ele é usado para inicializar quaisquer estados do fragmento.
     *
     * @param savedInstanceState estado salvo anteriormente do fragmento
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Este método é chamado para inflar a view do fragmento.
     * Ele usa um LayoutInflater para transformar o layout XML em uma view
     *
     * @param inflater LayoutInflater para inflar o layout
     * @param container o container onde a view será inserida
     * @param savedInstanceState estado salvo anteriormente do fragmento
     * @return a view criada do fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab1, container, false);
    }
}