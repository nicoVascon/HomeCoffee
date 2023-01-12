package ipleiria.pdm.homecoffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import ipleiria.pdm.homecoffee.ui.login.LoginActivity;

/**
 * Classe responsável por iniciar a Activity do SplashScreen
 */
public class SplashActivity extends AppCompatActivity {
    /**
     * Método chamado quando a activity é criada.
     *
     * Ele esconde a barra de ação e inicia um temporizador para o fechamento da tela
     * @param savedInstanceState - estado salvo da activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //This method will be executed once the timer is over
                // Start your app main activity
                System.out.println("Sai da Splash Activity");
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 2500);
    }
}