package ipleiria.pdm.homecoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;

public class RegisterActivity extends AppCompatActivity {


    private RecycleRoomsAdapter mAdapter;

    private Button loginButton;
    private EditText username;
    private EditText password;
    private EditText password2;
    FirebaseAuth firebaseAuth;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    @Override
    public void onStart() {
        super.onStart();


        mAdapter = new RecycleRoomsAdapter(this);
        firebaseAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.register_account);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        password2 = findViewById(R.id.password2);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_up();
            }
        });

    }

    private void sign_up() {
        String email = username.getText().toString();
        String pwd = password.getText().toString();
        String pwd2 = password2.getText().toString();

        Context context = this;

        if (email.isEmpty()) {

            username.setError("Please enter email id");
            username.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            username.setError("Please enter a valid email");
            username.requestFocus();
        } else if (pwd.isEmpty()) {
            password.setError("Please enter your password");
            password.requestFocus();
        } else if (pwd.length() < 6) {
            password.setError("Password should be at least 6 characters!");
            password.requestFocus();
        } else if (pwd2.isEmpty()) {
            password2.setError("Please confirm your password");
            password2.requestFocus();
        } else if (email.isEmpty() && pwd.isEmpty() && pwd2.isEmpty()) {
            Toast.makeText(this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
        } else if (!pwd.equals(pwd2)) {
            Toast.makeText(this, "Passwords dont match!", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email,
                    pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        if (task.getException() != null && task.getException() instanceof
                                FirebaseAuthUserCollisionException) {
                            Toast.makeText(context, "The email adress is already in use by another account!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(context, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                    } else {
                        User user = new User(email, email, pwd);


                        Intent switchActivityIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(switchActivityIntent);
                        //((MainActivity) mAdapter.getContext()).setInitialFragment();
                        finish();
                    }
                }
            });
        }


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //binding = null;
    }
}