package ipleiria.pdm.homecoffee.ui.gallery;

import static ipleiria.pdm.homecoffee.Enums.FragmentsEnum.GALLERY_FRAGMENT;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;

import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.mqtt.PahoDemo;
/**
 * Classe GalleryFragment é responsável por criar e gerenciar a interface de usuário para o fragmento de galeria.
 *
 * Ela também é responsável por gerenciar a comunicação entre o dispositivo Android e o gateway da casa (lopy).
 */
public class GalleryFragment extends Fragment {
    public static TextView textLogs;

    public EditText editTextMessage;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.app_name));
     
        textLogs = getView().findViewById(R.id.textLogs);
        editTextMessage = getView().findViewById(R.id.editTextMessage);
        Button buttonSubmit = getView().findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                HouseManager.addString_send_ttn(1, message);
                PahoDemo.getInstance().submitMessage();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.addFragmentViseted(GALLERY_FRAGMENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
            break;
        }
    }
}
