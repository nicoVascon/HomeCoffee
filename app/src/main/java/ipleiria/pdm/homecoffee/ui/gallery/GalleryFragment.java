package ipleiria.pdm.homecoffee.ui.gallery;

import android.content.Context;
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


import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ipleiria.pdm.homecoffee.HouseManager;
import ipleiria.pdm.homecoffee.MainActivity;

import ipleiria.pdm.homecoffee.R;
import ipleiria.pdm.homecoffee.mqtt.PahoDemo;

public class GalleryFragment extends Fragment {

    private HouseManager houseManager;
    private EditText editTextLopy;
    private Button button_lopy;


    //COmmunication between android and the house gateway(lopy)
    public static final String TAG = "msg";
    public static final String serverUri = "tcp://broker.hivemq.com:1883";
    public static final String subscriptionTopic = "messagesFromCroatia/#";
    public static final String publishTopic = "messagesFromCroatia";
    private String clientId = "MyAndroidClientId" + System.currentTimeMillis();

    public TextView getTextLogs() {
        return textLogs;
    }

    public void setTextLogs(TextView textLogs) {
        this.textLogs = textLogs;
    }

    public static TextView textLogs;
    public EditText editTextMessage;
    private static final int PERMISSION_REQUEST_CODE = 100;


    //TTN CONNECT MQTT
    MqttClient client_ttn;
    PahoDemo paho;


    @Override
    public void onStart() {
        super.onStart();
        Context context = this.getActivity();
        houseManager = HouseManager.getInstance();
        MainActivity.setCurrentFragment(this);
        MainActivity.setToolBarTitle(getResources().getString(R.string.app_name));

     
        textLogs = getView().findViewById(R.id.textLogs);
        editTextMessage = getView().findViewById(R.id.editTextMessage);

        //--------------------------MQTT---------------------

        Button buttonSubmit = getView().findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //TTN TRY
            String message = editTextMessage.getText().toString();
            HouseManager.addString_send_ttn(1, message);
            PahoDemo.getInstance().submitMessage();

            }
            });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }
    private void writeToFile(String data) {

        String filename ="mqtt_sharing.txt";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        try {
            FileOutputStream fos = new FileOutputStream(new File(dir,filename));
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(getActivity().getApplicationContext(),"Wrote to file: "+dir+filename,Toast.LENGTH_LONG).show();
            System.out.println("Wrote to file: "+dir+filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
        return true;
        } else {
        return false;
        }
        }

private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Toast.makeText(this.getActivity(), "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
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
