package tpsoa.soa.com.parkings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import tpsoa.soa.com.parkings.refactor.ParkingFirebase;
import tpsoa.soa.com.parkings.view.MapFragment;
import tpsoa.soa.com.parkings.view.ParkingListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    public static String TAG = "MainActivity";
    private int clickCounter;
    private SensorManager manager;
    private Sensor aSensor;
    private Sensor pSensor;
    private static final int SHAKE_THRESHOLD = 800;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    private float valorP;
    private boolean showInfoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickCounter = 0;
        lastUpdate = 0;
        last_x = last_y = last_z = valorP = 0;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //TODO Apenas iniciamos con la actividad principal cargamos el
        //fragmento con el mapa. Este fragmento no esta apilado
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_main, new MapFragment(), MapFragment.TAG)
                //.addToBackStack(null) esto arma una pila de fragmentos
                .commit();

        //TODO pedimos al sistema que nos provea del servicio de sensores
        //e instanciamos el acelerometro y proximidad
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        pSensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO los sensores los registramos en esta parte porque
        //es la ultima parte antes de la ejecucion donde siempre pasara el ciclo de vida
        manager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);

        manager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO si ponemos la app en segundo plano entonces paramos la escucha de sensores
        manager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        //TODO Esto controla el manu que se despliega desde la izquierda
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ParkingListFragment fragment =  (ParkingListFragment) getSupportFragmentManager()
                .findFragmentByTag(ParkingListFragment.TAG);

        //Si esta abierto entonces lo cierra,
        //sino preguntamos si hay algun fragmento visible y lo cerramos
        //o significa que quiere salir tocando el boton de "atras"
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment != null && fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        } else {
            //TODO si quiere salir entonces le damos 1,5 seg
            //para que vuelva a tocar atras y salga, sino
            //reiniciamos el contador y no termina la app
            clickCounter++;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SystemClock.sleep(1500);
                    clickCounter = 0;
                }
            }, 100);

            if (clickCounter == 2) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, getString(R.string.message_exit), Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //TODO estas opciones son del menu, no realizan ninguna accion
        //excepto la de nav_exit que es para deslogear la cuenta
        //e ir a SecondActivity donde tenemos los fragment de login y registro
        if (id == R.id.nav_exit) {
            //TODO SharedPreferences es un archivo preferencial xml
            //en el que podemos guardar pequeñas info dentro de la app
            //La usamos para saber si esta logeado y tener datos de usuarios guardados
            //como el token para firebase
            SharedPreferences preferences = getSharedPreferences(ParkingFirebase.TAG, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLogged", false);
            editor.commit();

            //inicio SecondActivity
            startActivity(new Intent(this, SecondActivity.class));
            //finalizo esta actividad
            finish();
        } else if (id == R.id.nav_texting) {
            //enviar notificaciones
        } else if (id == R.id.nav_setting) {
            //activar notficaciones
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //TODO eventos implementados de SensorEventListener
        //Obtenemos desde una region critica informacion de cada sensor
        //usamos synchronized para poder acceder a la region critica de
        //cada sensor, paraleliza los hilos, y obtenemos datos del
        //acelerometro y el sensor de proximidad que debe ser reflectivo
        showInfoWindow = false;
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long curTime = System.currentTimeMillis();

                //TODO calculamos la diferencia de tiempo para saber si
                //dentro de ese tiempo se agito el celular
                //y guardamos showInfoWindow=true para evaluar luego
                if ((curTime - lastUpdate) > 200) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float x = event.values[SensorManager.DATA_X];
                    float y = event.values[SensorManager.DATA_Y];
                    float z = event.values[SensorManager.DATA_Z];

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        showInfoWindow = true;
                        Log.e("sensor", "shake detected w/ speed: " + speed);
                    }
                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                valorP = event.values[0];
            }
        }


        if (showInfoWindow) {
            MapFragment fragment = (MapFragment) getSupportFragmentManager()
                    .findFragmentByTag(MapFragment.TAG);

            //TODO si se agito el celular entonces verificamos que el fragmento
            //del mapa este visible y luego realizamos acciones asociadas
            if (fragment != null && fragment.isVisible()) {
                fragment.actions();
            }
        }

        if (valorP == 0) {
            Log.e(MainActivity.TAG, "Proximidad " + valorP);
            MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.TAG);
            if (fragment != null && fragment.isVisible()) {
                ParkingListFragment parkingListFragment = (ParkingListFragment) getSupportFragmentManager()
                        .findFragmentByTag(ParkingListFragment.TAG);
                if (parkingListFragment == null || ! parkingListFragment.isVisible()) {
                    fragment.onMarkerClick(null);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
