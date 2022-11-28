package ipleiria.pdm.homecoffee;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

import ipleiria.pdm.homecoffee.Enums.FragmentsEnum;
import ipleiria.pdm.homecoffee.ui.Devices.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.AddDeviceSelectRoomFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryFragment;
import ipleiria.pdm.homecoffee.ui.home.AddHomeFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.slideshow.SlideshowFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private static TextView toolBarTitle;
    private static LinkedList<FragmentsEnum> lastsFragmentsOpened;
    private static boolean wasBackPressed;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private HouseManager houseManager;
    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolBarTitle.setText(getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            HouseManager.lerFicheiro(this);
            houseManager = HouseManager.getInstance();
            //houseManager.setrImage(android.R.drawable.btn_star_big_on);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.
                    MODE_NIGHT_NO);
            setInitialFragment();
        } else {
            houseManager = (HouseManager)
                    savedInstanceState.getSerializable("contactos");
        }

        lastsFragmentsOpened = new LinkedList<>();
        //saveLastFragmentOpened = true;
    }

    public void setInitialFragment() {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
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
                switch (lastsFragmentsOpened.pop()){
                    case HOME_FRAGMENT:
                        currentFragment = new HomeFragment();
                        break;
                    case DEVICES_FRAGMENT:
                        currentFragment = new DevicesFragment();
                        break;
                    case ADD_DEVICES_FRAGMENT:
                        currentFragment = new AddDeviceFragment();
                        break;
                    case ADD_ROOM_FRAGMENT:
                        currentFragment = new AddHomeFragment();
                        break;
                    case GALLERY_FRAGMENT:
                        currentFragment = new GalleryFragment();
                        break;
                    case SLIDES_HOW_FRAGMENT:
                        currentFragment = new SlideshowFragment();
                        break;
                    case ADD_DEVICES_SELECT_ROOM_FRAGMENT:
                        currentFragment = new AddDeviceSelectRoomFragment();
                        break;
                    default:
                        currentFragment = new HomeFragment();
                }
                wasBackPressed = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
                //saveLastFragmentOpened = true;
            }else{
                super.onBackPressed();
            }

        }
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
        lastsFragmentsOpened.addFirst(fragment);
    }

    public static void setWasBackPressed(boolean wasBackPressed) {
        MainActivity.wasBackPressed = wasBackPressed;
    }

    //    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}