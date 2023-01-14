package ipleiria.pdm.homecoffee;

import static java.lang.Boolean.TRUE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.components.LoadingDialog;
import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.model.Device;
import ipleiria.pdm.homecoffee.model.Room;
import ipleiria.pdm.homecoffee.mqtt.PahoDemo;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceSelectRoomFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceSelectSensorFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.EditDeviceSelectSensorFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryFragment;
import ipleiria.pdm.homecoffee.ui.rooms.AddRoomFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.login.LoginActivity;
import ipleiria.pdm.homecoffee.ui.slideshow.SlideshowFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private static TextView toolBarTitle;
    private static LinkedList<FragmentsEnum> lastsFragmentsOpened = new LinkedList<>();;
    private static boolean wasBackPressed;
    private static boolean wasRotated;
    private static Fragment currentFragment;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE = 101;

    private static final int REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1;
    List<String> permissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        Activity activity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolBarTitle.setText(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        HouseManager.lerFicheiro(this);

        //Permissions  to ask for
        permissions = new ArrayList<>();
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        permissions.add(Manifest.permission.BLUETOOTH_SCAN);
        permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (Build.VERSION.SDK_INT < 30) {

            permissions.add(Manifest.permission.BLUETOOTH);
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        //Method that asks for permissions
        checkBluetoothPermissions();

        mAuth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);
        TextView textHeader = header.findViewById(R.id.textViewUser);

        setInitialFragment();
//        if (savedInstanceState == null) {
//            //houseManager.setrImage(android.R.drawable.btn_star_big_on);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.
//                    MODE_NIGHT_NO);
//            //setInitialFragment();
//            if(!HouseManager.getInstance().isLoginMade()){
//                setLoginFragment();
//            }
//            else {
//                setInitialFragment();
//            }
//        } else {
//            //houseManager = (HouseManager) savedInstanceState.getSerializable("contactos");
//        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PahoDemo.getInstance().start_mqtt(activity);
                while(true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (HouseManager.isModificable()){
                        PahoDemo.getInstance().submitMessage();
                    }

                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(GalleryFragment.textLogs!=null){
                                StringBuilder msgs_received = HouseManager.getInstance().getMsgs_received();
                                GalleryFragment.textLogs.setText(msgs_received.toString());
                            }
                            DeviceActivityFragment.updateValues();
                        }
                    });
                }
            }
        }) ;
        thread.start();


    }

    private void setCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user_mail = currentUser.getEmail();
        User user = new User(user_mail);
        HouseManager.getInstance().setUser(user);

    }

    public void setInitialFragment() {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    public void setLoginFragment() {
        System.out.println("Estou a ir para o Login, LoginFragment()");
        Intent switchActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("contactos", HouseManager.getInstance());
    }
    @Override
    protected void onPause() {
        super.onPause();
        HouseManager.gravarFicheiro(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle bundle = currentFragment.getArguments();
        switch (item.getItemId()) {
            case R.id.nav_home:
                currentFragment = new HomeFragment();
                break;
            case R.id.nav_gallery:
                currentFragment = new GalleryFragment();
                break;
            case R.id.nav_slideshow:
                currentFragment = new SlideshowFragment();
                break;
            case R.id.nav_devices:
                currentFragment = new DevicesFragment();
                break;
            default:
                currentFragment = new HomeFragment();
        }
        currentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!(currentFragment instanceof HomeFragment)) {
                if (currentFragment instanceof SaveData){
                    ((SaveData) currentFragment).saveData();
                }
                switch (lastsFragmentsOpened.pop()){
                    case HOME_FRAGMENT:
                        navigationView.setCheckedItem(R.id.nav_home);
                        currentFragment = new HomeFragment();
                        break;
                    case DEVICES_FRAGMENT:
                        navigationView.setCheckedItem(R.id.nav_devices);
                        currentFragment = new DevicesFragment();
                        break;
                    case ADD_DEVICES_FRAGMENT:
                        currentFragment = new AddDeviceFragment();
                        break;
                    case ADD_ROOM_FRAGMENT:
                        currentFragment = new AddRoomFragment();
                        break;
                    case GALLERY_FRAGMENT:
                        navigationView.setCheckedItem(R.id.nav_gallery);
                        currentFragment = new GalleryFragment();
                        break;
                    case SLIDES_HOW_FRAGMENT:
                        navigationView.setCheckedItem(R.id.nav_slideshow);
                        currentFragment = new SlideshowFragment();
                        break;
                    case ADD_DEVICES_SELECT_ROOM_FRAGMENT:
                        currentFragment = new AddDeviceSelectRoomFragment();
                        break;
                    case DEVICE_DETAILS_FRAGMENT:
                        ArrayList<Device> devicesArrayList = HouseManager.getInstance().getDevices();
                        if(HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
                            int roomPosition = HouseManager.getBundle().getInt(HomeFragment.RESULT_ROOM_POSITION);
                            Room room = HouseManager.getInstance().getRoom(roomPosition);
                            devicesArrayList = room.getDevices();
                        }
                        currentFragment = new DeviceDetailsFragment(devicesArrayList);
                        break;
                    case ADD_DEVICES_SELECT_SENSOR_FRAGMENT:
                        currentFragment = new AddDeviceSelectSensorFragment();
                        break;
                    case EDIT_DEVICES_SELECT_ROOM_FRAGMENT:
                        currentFragment = new EditDeviceSelectSensorFragment();
                        break;
                    default:
                        currentFragment = new HomeFragment();
                }
                wasBackPressed = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
            }else{
                super.onBackPressed();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public static void setToolBarTitle(String title){
        toolBarTitle.setText(title);
    }

    public static void addFragmentViseted(FragmentsEnum fragment){
        if (wasBackPressed){
            wasBackPressed = false;
            return;
        }
        if (wasRotated){
            wasRotated = false;
            return;
        }
        lastsFragmentsOpened.addFirst(fragment);
    }

    public static void clearFragmentsVisitedList(){
        lastsFragmentsOpened.clear();
        lastsFragmentsOpened.addFirst(FragmentsEnum.HOME_FRAGMENT);
    }

    public static void setCurrentFragment(Fragment currentFragment) {
        MainActivity.currentFragment = currentFragment;
    }

    public static Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void btn_logout(View view) {
        FirebaseAuth.getInstance().signOut();
        setLoginFragment();
        drawer.closeDrawer(GravityCompat.START);
        //finish();
    }

    private void checkBluetoothPermissions() {
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (!allPermissionsGranted) {
            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth Permissions")
                    .setMessage("This app needs access to Bluetooth and location to function properly. Please grant the permissions.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions.toArray(permissions.toArray(new String[permissions.size()])), REQUEST_CODE_BLUETOOTH_PERMISSIONS);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
//            boolean allGranted = true;
//            for (int grantResult : grantResults) {
//                if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    break;
//                }
//            }
//            if (allGranted) {
//                // Permissions were granted, continue with the Bluetooth functionality
//            } else {
//                // Permissions were denied, show a message to the user
//                Toast.makeText(this, "Permissions were denied, Bluetooth features won't work", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (permission.equals(Manifest.permission.BLUETOOTH)) {
                        Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
                    } else if (permission.equals(Manifest.permission.BLUETOOTH_ADMIN)) {
                        Toast.makeText(this, "Bluetooth admin permission denied", Toast.LENGTH_SHORT).show();
                    } else if (permission.equals(Manifest.permission.BLUETOOTH_SCAN)) {
                        Toast.makeText(this, "Bluetooth scan permission denied", Toast.LENGTH_SHORT).show();
                    } else if (permission.equals(Manifest.permission.BLUETOOTH_ADVERTISE)) {
                        Toast.makeText(this, "Bluetooth advertise permission denied", Toast.LENGTH_SHORT).show();
                    } else if (permission.equals(Manifest.permission.BLUETOOTH_CONNECT)) {
                        Toast.makeText(this, "Bluetooth connect permission denied", Toast.LENGTH_SHORT).show();
                    } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(this, "Access fine location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void button_send_loopy(View view) {

    /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            System.out.println("Oi entrei e vou publicar");
            client.publishWith()
                    .topic("v1/411755b0-58fa-11ed-bf0a-bb4ba43bd3f6/things/5c9496e0-58ff-11ed-bf0a-bb4ba43bd3f6/data/3")
                    .payload("digital_sensor,d=1".getBytes())
                    .send()
                    .whenComplete((publish, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Nao publiquei o mundo");
                            System.out.println(throwable.getMessage());
                        } else {

                            System.out.println("Publqiuei o mundo");
                        }
                    });
        }*/

    }
}