package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ipleiria.pdm.homecoffee.R;

/**
 * Classe adapter para a exibição de abas utilizando FragmentStateAdapter.
 * Permite adicionar fragmentos e títulos de aba para exibir.
 */
public class TabAdapter extends FragmentStateAdapter {
    /**
     * Lista de fragmentos que serão exibidos na tab.
     */
    private final List<Fragment> mFragmentList = new ArrayList<>();
    /**
     * Lista dos títulos dos fragmentos.
     */
    private final List<String> mFragmentTitleList = new ArrayList<>();

    /**
     * Construtor.
     * @param fragment O fragmento base para o qual o adapter é criado.
     */
    public TabAdapter(Fragment fragment) {
        super(fragment);
    }
    /**
     * Adiciona um fragmento e um título de aba.
     * @param fragment O fragmento a ser adicionado.
     * @param title O título da aba para o fragmento.
     */
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    /**
     * Cria o fragmento para a posição especificada.
     * @param position A posição para qual o fragmento deve ser criado.
     * @return O fragmento para a posição especificada.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    /**
     * @return O número de itens (fragmentos) no adapter.
     */
    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}
