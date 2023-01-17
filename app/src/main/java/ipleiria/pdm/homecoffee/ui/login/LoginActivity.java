package ipleiria.pdm.homecoffee.ui.login;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.RegisterActivity;
import ipleiria.pdm.homecoffee.User;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;

/**
 * Classe da atividade de login.
 * Estende a classe AppCompatActivity.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Adapter do RecyclerView que exibe as salas.
     */
    private RecycleRoomsAdapter mAdapter;

    /**
     * Botão de login.
     */
    private Button loginButton;
    /**
     * Campo de texto para inserção do nome de usuário.
     */
    private EditText username;
    /**
     * Campo de texto para inserção da senha.
     */
    private EditText password;

    /**
     * Instância de FirebaseAuth para autenticação do usuário.
     */
    FirebaseAuth firebaseAuth;
    /**
     * TextView para redirecionar o usuário para a tela de registro.
     */
    private TextView register;
    /**
     * Instância de HouseManager para gerenciamento das salas.
     */
    private HouseManager houseManager;

    /**
     * Método chamado na criação da atividade.
     * Configura a interface gráfica, esconde a ActionBar, instancia um adapter para o RecyclerView,
     * instancia um objeto FirebaseAuth para autenticação do usuário, inicializa os campos de texto de nome de usuário e senha,
     * e os botões de login e registro.Método chamado na criação da atividade.
     * Configura a interface gráfica, esconde a ActionBar, instancia um adapter para o RecyclerView,
     * instancia um objeto FirebaseAuth para autenticação do usuário, inicializa os campos de texto de nome de usuário e senha,
     * e os botões de login e registro.
     * Também verifica se o usuário já está logado e, se sim, direciona para a MainActivity.
     * @param savedInstanceState é o estado anterior do fragmento, caso exista.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        System.out.println("Criei o login activity");

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.register_account);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);


        FirebaseUser user = firebaseAuth.getCurrentUser();


        if(user != null){
            HouseManager.getInstance().setLoginMade(true);
            Toast.makeText(this, "You are Logged in", Toast.LENGTH_SHORT).show();
            HouseManager.getInstance().setCurrentUser(user);
            Intent switchActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(switchActivityIntent);
            System.out.println("Vou entrar no main activity a partir do login activity");
            finish();
            return;
        }

        HouseManager.getInstance().setLoginMade(false);
        Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sing_in();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent switchActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(switchActivityIntent);
                finish();
            }
        });

    }

    /**
     * Método que realiza a autenticação do usuário.
     * Verifica se os campos de email e senha estão vazios, se a senha tem menos de 6 caracteres,
     * e se os campos estão vazios. Em caso positivo, exibe mensagens de erro.
     * Caso contrário, utiliza o FirebaseAuth para realizar a autenticação e inicia a MainActivity.
     */
    private void sing_in() {
        String email = username.getText().toString();
        String pwd = password.getText().toString();

        if (email.isEmpty() && pwd.isEmpty()) {
            Toast.makeText(this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            username.setError("Please enter email id");
            username.requestFocus();
            return;
        }
        if (pwd.isEmpty()) {
            password.setError("Please enter your password");
            password.requestFocus();
            return;
        }
        if (pwd.length() < 6) {
            password.setError("The given password is invalid. Password should be at least 6 characters!");
            password.requestFocus();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email,
                pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "You are Logged in",
                            Toast.LENGTH_SHORT).show();
                    HouseManager.getInstance().setUser(new User(email));
                    Intent switchActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(switchActivityIntent);
                    finish();
                }
            }
        });
    }
    /**
     * Método onCreateView() é chamado quando o fragmento é criado. Ele infla o layout da activity_login
     * e o retorna como a visualização do fragmento.
     * @param inflater objeto LayoutInflater que é usado para inflar o layout do fragmento
     * @param container objeto ViewGroup que é o contêiner do fragmento
     * @param savedInstanceState objeto Bundle que contém o estado salvo anteriormente do fragmento
     * @return a visualização do fragmento
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_login, container, false);
    }

    /**
     * Método onDestroy() é chamado quando o fragmento é destruído.
     * Ele chama o método super.onDestroy() da classe pai.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}