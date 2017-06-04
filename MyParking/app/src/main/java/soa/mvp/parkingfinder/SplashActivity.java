package soa.mvp.parkingfinder;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
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

import soa.mvp.parkingfinder.refactor.ParkingFirebase;

public class SplashActivity extends AppCompatActivity implements Animator.AnimatorListener {
    protected Animation fadeIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);
        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        ImageView imageView = (ImageView) findViewById(R.id.splash_imageView);
        imageView.setVisibility(View.VISIBLE);
        imageView.startAnimation(fadeIn);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SystemClock.sleep(1500);

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

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
