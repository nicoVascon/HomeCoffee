package ipleiria.pdm.homecoffee;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;

/**
 * Classe que fornece métodos para verificar a conexão à internet.
 */
public class InternetConnection {

    /**
     * erifica se a conexão à internet está disponível.
     * @param context contexto da aplicação.
     * @return true se houver conexão, false caso contrário.
     */
    public static boolean checkConnection(@NonNull Context context) {
        return  ((ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
