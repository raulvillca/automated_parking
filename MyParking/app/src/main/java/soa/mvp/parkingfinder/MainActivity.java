package soa.mvp.parkingfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import soa.mvp.parkingfinder.model.Time;
import soa.mvp.parkingfinder.refactor.ParkingFirebase;
import soa.mvp.parkingfinder.view.MapFragment;
import soa.mvp.parkingfinder.view.ParkingListFragment;

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

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_main, new MapFragment(), MapFragment.TAG)
                .commit();

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        pSensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ParkingListFragment fragment =  (ParkingListFragment) getSupportFragmentManager()
                .findFragmentByTag(ParkingListFragment.TAG);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment != null && fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        } else {
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

        if (id == R.id.nav_exit) {
            SharedPreferences preferences = getSharedPreferences(ParkingFirebase.TAG, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLogged", false);
            editor.commit();

            startActivity(new Intent(this, SecondActivity.class));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            List<String> matches = data.getStringArrayListExtra
                    (RecognizerIntent.EXTRA_RESULTS);
            //Separo el texto en palabras.
            String palabras = matches.get(0).toString();

            Log.e("Mostrar palabras", palabras);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 100) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float x = event.values[SensorManager.DATA_X];
                    float y = event.values[SensorManager.DATA_Y];
                    float z = event.values[SensorManager.DATA_Z];

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        Log.e("sensor", "shake detected w/ speed: " + speed);
                        MapFragment fragment = (MapFragment) getSupportFragmentManager()
                                .findFragmentByTag(MapFragment.TAG);

                        if (fragment != null && fragment.isVisible()) {
                            fragment.showInfoWindow();
                        }
                    }
                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                valorP = event.values[0];
                Log.e(MainActivity.TAG, "Proximidad " + valorP);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
