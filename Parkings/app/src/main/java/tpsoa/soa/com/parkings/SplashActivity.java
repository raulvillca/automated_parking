package tpsoa.soa.com.parkings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import tpsoa.soa.com.parkings.refactor.ParkingFirebase;

public class SplashActivity extends AppCompatActivity {
    protected Animation animation;
    private ImageView imageView_indicator;
    private ImageView imageView_car;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);
        animation = AnimationUtils.loadAnimation(this, R.anim.bottom_car_animation);
        imageView_car = (ImageView) findViewById(R.id.splash_imageView);
        imageView_car.setVisibility(View.VISIBLE);
        imageView_car.startAnimation(animation);

        imageView_indicator = (ImageView) findViewById(R.id.splash_imageView_indicator);
        imageView_indicator.setBackground(getResources().getDrawable(R.drawable.ic_rv_hookup_green));


        //TODO En este hilo verificamos si el usuario ya esta logeado,
        //si lo esta entonces iniciamos MainActivity sino vamos a SecondActivity
        new RedIndicatorTask().execute();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SystemClock.sleep(2500);

                SharedPreferences sharedPreferences = getSharedPreferences(ParkingFirebase.TAG, MODE_PRIVATE);
                if (sharedPreferences.getBoolean("isLogged", false))
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, SecondActivity.class));

                finish();
            }
        };
        new Timer().schedule(timerTask, 100);
    }

    //TODO primer movimiento del auto estacionandose
    public class RedIndicatorTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(1200);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_car_animation);
            imageView_car.startAnimation(animation);
            imageView_indicator.setBackgroundResource(R.drawable.ic_rv_hookup_red);
            new GreenIndicatorTask().execute();
        }
    }

    //TODO utilizamos este hilo para realizar la siguiente secuencia de movimiento del auto
    public class GreenIndicatorTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(700);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_car_animation);
            imageView_car.startAnimation(animation);
            imageView_indicator.setBackgroundResource(R.drawable.ic_rv_hookup_green);
        }
    }
}
