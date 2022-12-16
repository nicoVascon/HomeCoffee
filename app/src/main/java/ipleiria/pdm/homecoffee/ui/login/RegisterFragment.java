package ipleiria.pdm.homecoffee.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.User;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;


public class RegisterFragment extends Fragment {


    private RecycleRoomsAdapter mAdapter;

    private Button loginButton;
    private EditText username;
    private EditText password;
    private EditText password2;
    FirebaseAuth firebaseAuth;
    private TextView register;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }



    @Override
    public void onStart() {
        super.onStart();


        mAdapter = new RecycleRoomsAdapter(this.getActivity());
        firebaseAuth = FirebaseAuth.getInstance();

        register = getView().findViewById(R.id.register_account);
        username = getView().findViewById(R.id.username);
        password = getView().findViewById(R.id.password);

        password2 = getView().findViewById(R.id.password2);



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

        Context context = getContext();

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
            Toast.makeText(this.getContext(), "Fields Are Empty!", Toast.LENGTH_SHORT).show();
        } else if (!pwd.equals(pwd2)) {
            Toast.makeText(this.getContext(), "Passwords dont match!", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email,
                    pwd).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
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

                        ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    }
                }
            });
        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }

}