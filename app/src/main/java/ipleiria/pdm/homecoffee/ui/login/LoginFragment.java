package ipleiria.pdm.homecoffee.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import ipleiria.pdm.homecoffee.MainActivity;
import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.adapter.RecycleRoomsAdapter;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;


public class LoginFragment extends Fragment {

    private RecycleRoomsAdapter mAdapter;

    private Button loginButton;
    private EditText username;
    private EditText password;

    FirebaseAuth firebaseAuth;
    private TextView register;

    public LoginFragment() {
        // Required empty public constructor
    }




    @Override
    public void onStart() {
        super.onStart();


        mAdapter = new RecycleRoomsAdapter(this.getActivity());
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton = getView().findViewById(R.id.register_account);
        username = getView().findViewById(R.id.username);
        password = getView().findViewById(R.id.password);
        register = getView().findViewById(R.id.register);

        if (firebaseAuth.getCurrentUser() == null)
            Toast.makeText(this.getContext(), "Please login", Toast.LENGTH_SHORT).show();
        else
        {
            Toast.makeText(this.getContext(), "You are Logged in", Toast.LENGTH_SHORT).show();
            ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  sing_in();  
                //((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
            }
        });

    }

    private void sing_in() {
        String email = username.getText().toString();
        String pwd = password.getText().toString();
        Context context = getContext();


        if (email.isEmpty()) {
            username.setError("Please enter email id");
            username.requestFocus();
        } else if (pwd.isEmpty()) {
            password.setError("Please enter your password");
            password.requestFocus();
        } else if (pwd.length() < 6) {
            password.setError("The given password is invalid. Password should be at least 6 characters!");
            password.requestFocus();
        } else if (email.isEmpty() && pwd.isEmpty()) {
            Toast.makeText(this.getContext(), "Fields Are Empty!", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email,
                    pwd).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "You are Logged in",
                                Toast.LENGTH_SHORT).show();

                        ((MainActivity) mAdapter.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    }
                }
            });
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }
}