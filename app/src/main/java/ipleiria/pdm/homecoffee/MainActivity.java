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
import ipleiria.pdm.homecoffee.mqtt.MyForegroundService;
import ipleiria.pdm.homecoffee.mqtt.PahoDemo;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceSelectRoomFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Add.AddDeviceSelectSensorFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceActivityFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.DeviceControlFragment;
import ipleiria.pdm.homecoffee.ui.Devices.Details.EditDeviceSelectSensorFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DeviceDetailsFragment;
import ipleiria.pdm.homecoffee.ui.Devices.DevicesFragment;
import ipleiria.pdm.homecoffee.ui.gallery.GalleryFragment;
import ipleiria.pdm.homecoffee.ui.rooms.AddRoomFragment;
import ipleiria.pdm.homecoffee.ui.home.HomeFragment;
import ipleiria.pdm.homecoffee.ui.login.LoginActivity;
import ipleiria.pdm.homecoffee.ui.slideshow.SlideshowFragment;

/**
 * Classe MainActivity é a classe principal da aplicação onde é iniciado a aplicação.
 * É responsável por gerenciar a navegação entre as diferentes telas (fragments) da aplicação.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    /**
     * Atributo toolBarTitle é um TextView responsável por armazenar o título da toolbar.
     */
    private static TextView toolBarTitle;
    /**
     * Atributo lastsFragmentsOpened é uma LinkedList que armazena os últimos fragments abertos.
     */
    private static LinkedList<FragmentsEnum> lastsFragmentsOpened = new LinkedList<>();;
    /**
     * Atributo wasBackPressed indica se o botão de voltar foi pressionado.
     */
    private static boolean wasBackPressed;
    /**
     * Atributo wasRotated indica se a tela foi rotacionada.
     */
    private static boolean wasRotated;
    /**
     * Atributo currentFragment armazena o fragmento atual.
     */
    private static Fragment currentFragment;
    /**
     * Atributo drawer é um DrawerLayout responsável por gerenciar a exibição do menu lateral.
     */
    private DrawerLayout drawer;
    /**
     * Atributo navigationView é um NavigationView responsável por gerenciar as opções do menu lateral.
     */
    private NavigationView navigationView;
    /**
     * Atributo houseManager é uma instância da classe HouseManager responsável por gerenciar os dados da aplicação.
     */
    private HouseManager houseManager;

    /**
     * Atributo mAuth é uma instância de autenticação do Firebase
     */
    private FirebaseAuth mAuth;
    /**
     * Código de requisição utilizado para acessar funcionalidades do dispositivo relacionadas ao Bluetooth.
     */
    private static final int REQUEST_CODE = 101;
    /**
     * Código de requisição utilizado para solicitar permissões de acesso ao Bluetooth do dispositivo.
     */
    private static final int REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1;
    /**
     * Lista de permissões necessárias para o acesso ao Bluetooth.
     */
    List<String> permissions;

    /**
     * Método onCreate da classe MainActivity, é chamado quando a classe é instanciada. Recebe como argumento um objeto do tipo Bundle que contém informações sobre o estado da aplicação.
     *
     * @param savedInstanceState objeto do tipo Bundle que contém informações sobre o estado da aplicação.
     */
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

        //Permissions to ask for
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
//        TextView textHeader = header.findViewById(R.id.textViewUser);
        TextView textEmailUser = header.findViewById(R.id.textViewUserEmail);
        textEmailUser.setText(mAuth.getCurrentUser().getEmail());
        setInitialFragment();



        //runForever();

        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        startService(serviceIntent);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    (MainActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(GalleryFragment.textLogs!=null){
                                StringBuilder msgs_received = HouseManager.getInstance().getMsgs_received();
                                GalleryFragment.textLogs.setText(msgs_received.toString());
                            }
                            DeviceActivityFragment.updateValues();
                            DeviceControlFragment.updateSensorValue();
                            DevicesFragment.updateDevicesValues();
                        }
                    });
                }
            }
        }) ;
        thread.start();


    }
    /**
     *
     * Utilizado para definir o utilizador atual autenticado na aplicação.
     * É atribuida uma instância de FirebaseUser com o utilizador atual e é atribuido o endereço de email a uma nova instância de User.
     * Esse objeto User é adicionado à instância de HouseManager para uso futuro.
     */
    private void setCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user_mail = currentUser.getEmail();
        User user = new User(user_mail);
        HouseManager.getInstance().setUser(user);

    }
    /**
     * Utilizado para definir o fragmento inicial mostrado na aplicação.
     * O fragmento HomeFragment é adicionado ao container de fragmentos e o item de navegação correspondente é marcado como selecionado.
     */
    public void setInitialFragment() {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    /**
     * Utilizado para definir o fragmento de login na aplicação.
     * É iniciada uma nova atividade para o LoginActivity e a atividade atual é finalizada.
     */
    public void setLoginFragment() {
        System.out.println("Estou a ir para o Login, LoginFragment()");
        Intent switchActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    /**
     * Ele salva o estado atual da aplicação para que possa ser restaurado quando a atividade for criada novamente.
     * @param outState Objeto Bundle onde o estado atual da aplicação é salvo.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable("contactos", HouseManager.getInstance());
    }

    /**
     * Método onPause é chamado quando a atividade está prestes a ser pausada.
     * Ele é usado para salvar o estado atual da aplicação em um arquivo.
     */
    @Override
    protected void onPause() {
        super.onPause();
//        HouseManager.gravarFicheiro(this);
    }

    /**
     * Método onNavigationItemSelected é chamado quando um item de navegação é selecionado.
     * Ele é usado para navegar entre os fragmentos da aplicação.
     * @param item Objeto MenuItem que representa o item selecionado.
     * @return true se a navegação foi bem-sucedida, false caso contrário.
     */
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

    /**
     * Verifica se o Drawer está aberto e o fecha, caso contrário verifica se o fragmento atual é o HomeFragment,
     * se não for, é verificado o último fragmento aberto na stack e é mostrado esse fragmento.
     */
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
//                        ArrayList<Device> devicesArrayList = HouseManager.getInstance().getDevices();
//                        if(HouseManager.getBundle().containsKey(HomeFragment.RESULT_ROOM_POSITION)){
//                            int roomPosition = HouseManager.getBundle().getInt(HomeFragment.RESULT_ROOM_POSITION);
//                            Room room = HouseManager.getInstance().getRoom(roomPosition);
//                            devicesArrayList = room.getDevices();
//                        }
                        currentFragment = new DeviceDetailsFragment();
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

    /**
     * Método de destruição da classe.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Método chamado quando o menu é criado. Infla o menu com os itens presentes na action bar.
     * @param menu O menu que será criado.
     * @return Retorna true se o menu for criado com sucesso.
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    /**
     * Método para mudar o título da toolbar.
     * @param title O novo título da toolbar.
     */
    public static void setToolBarTitle(String title){
        toolBarTitle.setText(title);
    }

    /**
     * Método para adicionar um fragmento visitado à lista de fragmentos visitados.
     * @param fragment O fragmento visitado.
     */
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

    /**
     * Método para limpar a lista de fragmentos visitados.
     */
    public static void clearFragmentsVisitedList(){
        lastsFragmentsOpened.clear();
        lastsFragmentsOpened.addFirst(FragmentsEnum.HOME_FRAGMENT);
    }

    /**
     * Método para definir o fragmento atual.
     * @param currentFragment O fragmento atual.
     */
    public static void setCurrentFragment(Fragment currentFragment) {
        MainActivity.currentFragment = currentFragment;
    }

    /**
     * Método para obter o fragmento atual
     * @return o fragmento atual
     */
    public static Fragment getCurrentFragment() {
        return currentFragment;
    }

    /**
     * Método que é chamado quando o botão de logout é pressionado.
     * Ele desconecta o usuário atual do FirebaseAuth, chama o método setLoginFragment para mudar para a tela de login,
     * fecha o menu lateral e finaliza a atividade atual.
     * @param view O objeto da view associado ao botão de logout
     */
    public void btn_logout(View view) {
        FirebaseAuth.getInstance().signOut();
        setLoginFragment();
        drawer.closeDrawer(GravityCompat.START);
        //finish();
    }

    /**
     * Método que verifica as permissões dadas e as pede se não as tiver já
     */
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