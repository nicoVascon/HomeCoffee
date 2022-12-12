package ipleiria.pdm.homecoffee;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.interfaces.SaveData;
import ipleiria.pdm.homecoffee.ui.Devices.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.AddDeviceSelectRoomFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryFragment;
import ipleiria.pdm.homecoffee.ui.home.AddRoomFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.login.LoginFragment;
import ipleiria.pdm.homecoffee.ui.slideshow.SlideshowFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private static TextView toolBarTitle;
    private static LinkedList<FragmentsEnum> lastsFragmentsOpened = new LinkedList<>();;
    private static boolean wasBackPressed;
    private static boolean wasRotated;
    private static Fragment currentFragment;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private HouseManager houseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolBarTitle.setText(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        HouseManager.lerFicheiro(this);
        houseManager = HouseManager.getInstance();



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

        if (savedInstanceState == null) {


            //houseManager.setrImage(android.R.drawable.btn_star_big_on);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.
                    MODE_NIGHT_NO);
            //setInitialFragment();
            if(houseManager.isLoginMade()!=TRUE){

                setLoginFragment();

            }
            else {
                setInitialFragment();

            }
        } else {
            houseManager = (HouseManager)
                    savedInstanceState.getSerializable("contactos");
        }
        //saveLastFragmentOpened = true;
    }

    public void setInitialFragment() {
        getSupportActionBar().show();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawer.closeDrawer(GravityCompat.START);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    public void setLoginFragment() {

        //getSupportFragmentManager().beginTransaction().replace(
        //        R.id.fragment_container, new LoginFragment()).commit();
        System.out.println("Estou a ir para o Login, LoginFragment()");
        Intent switchActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(switchActivityIntent);
        finish();
        //getSupportActionBar().hide();
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("contactos", houseManager);
    }
    @Override
    protected void onPause() {
        super.onPause();
        houseManager.gravarFicheiro(this);
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
                        currentFragment = new DeviceDetailsFragment();
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
}