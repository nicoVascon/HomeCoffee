package ipleiria.pdm.homecoffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import ipleiria.pdm.homecoffee.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

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