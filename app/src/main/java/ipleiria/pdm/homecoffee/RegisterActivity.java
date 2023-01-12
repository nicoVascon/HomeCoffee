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

/**
 * Classe RegisterActivity é responsável por criar uma tela de registo de usuário.
 *
 * Ela herda de AppCompatActivity e é utilizada para criar uma interface para o usuário
 * realizar o registro na aplicação.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * RecycleRoomsAdapter mAdapter é uma instância da classe RecycleRoomsAdapter
     * que irá adaptar a lista de salas para o RecyclerView.
     */
    private RecycleRoomsAdapter mAdapter;
    /**
     * Button loginButton é um botão que é utilizado para realizar o registro do usuário.
     */
    private Button loginButton;
    /**
     * EditText username é um campo de texto onde o usuário irá inserir o nome de usuário.
     */
    private EditText username;
    /**
     * EditText password é um campo de texto onde o usuário irá inserir a senha de acesso.
     */
    private EditText password;
    /**
     * EditText password2 é um campo de texto onde o usuário irá confirmar a senha de acesso.
     */
    private EditText password2;
    /**
     * FirebaseAuth firebaseAuth é uma instância da classe FirebaseAuth que é utilizada
     * para realizar o registro do usuário no banco de dados do Firebase.
     */
    FirebaseAuth firebaseAuth;
    /**
     * TextView register é um texto exibido na tela que informa ao usuário sobre a necessidade
     */
    private TextView register;

    /**
     * Método onCreate é chamado quando a activity é criada. Ele é responsável por
     * inicializar as views e configurar os listeners dos botões.
     * @param savedInstanceState é o estado salvo anteriormente da activity (opcional).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Método chamado quando a activity começa. Nela é inicializado o adapter,
     * a instância do firebaseAuth e os listeners dos botões e campos de texto.
     */
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

    /**
     * Método que é executado ao clicar no botão de registo, que verifica se os dados inseridos são válidos e se estão de acordo com as regras de registo.
     * Se os dados estiverem corretos, é criada uma conta de utilizador com os dados fornecidos e é iniciada a MainActivity
     */
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
                        User user = new User(email);


                        Intent switchActivityIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(switchActivityIntent);
                        //((MainActivity) mAdapter.getContext()).setInitialFragment();
                        finish();
                    }
                }
            });
        }


    }


    /**
     * Método que é chamado na destruição da actividade
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //binding = null;
    }
}