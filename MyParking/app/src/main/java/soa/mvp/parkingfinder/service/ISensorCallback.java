package soa.mvp.parkingfinder.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by raulvillca on 20/5/17.
 */

public class ISensorCallback implements SensorEventListener {
    private static String TAG = "ISensorCallback";
    private Fragment fragment;
    private SensorCallback callback;
    private SensorManager mSensorManager;
    private Sensor pSensor;
    private Sensor gSensor;

    public ISensorCallback(Fragment fragment) {
        this.fragment = fragment;
        this.callback = (SensorCallback) fragment;
        mSensorManager = (SensorManager) fragment.getActivity().getSystemService(fragment.getActivity().SENSOR_SERVICE);
        pSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void init() {
        mSensorManager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    this.callback.onClick();
                    break;
                case Sensor.TYPE_PROXIMITY:
                    this.callback.onClick();
                    break;
            }

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                Log.d(TAG, event.values[0] + " " + event.values[1] + " " + event.values[2]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
