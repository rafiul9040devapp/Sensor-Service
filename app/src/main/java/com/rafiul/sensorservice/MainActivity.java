package com.rafiul.sensorservice;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.rafiul.sensorservice.database.SensorData;
import com.rafiul.sensorservice.database.SensorDatabase;
import com.rafiul.sensorservice.databinding.ActivityMainBinding;
import com.rafiul.sensorservice.series.LinearChart;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding activityMainBinding;

    private SensorManager sensorManager;
    private Sensor lightSensor, proximitySensor, accelerometerSensor, gyroscopeSensor;

    float proximityValue, lightSensorValue;
    float[] accelerometerValue, gyroscopeValue;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private SensorDatabase sensorDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, (int) TimeUnit.MINUTES.toMicros(5));
        }

        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, (int) TimeUnit.MINUTES.toMicros(5));
        }

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, (int) TimeUnit.MINUTES.toMicros(5));
        }

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, (int) TimeUnit.MINUTES.toMicros(5));
        }


        sensorDatabase = SensorDatabase.getSensorDataBase(getApplicationContext());

        activityMainBinding.tvProximitySensor.setOnClickListener(v -> {
            Intent intent = new Intent(this, LinearChart.class);
            intent.putExtra("Sensor", "Proximity");
            startActivity(intent);
        });
        activityMainBinding.tvLightSensor.setOnClickListener(v -> {
            Intent intent = new Intent(this, LinearChart.class);
            intent.putExtra("Sensor", "Light");
            startActivity(intent);
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityValue = sensorEvent.values[0];
            activityMainBinding.tvProximitySensor.setText("PROXIMITY SENSOR: " + proximityValue);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightSensorValue = sensorEvent.values[0];
            activityMainBinding.tvLightSensor.setText("LIGHT SENSOR: " + lightSensorValue);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValue = sensorEvent.values;
            activityMainBinding.tvAccelerometer.setText("ACCELEROMETER: X=" + accelerometerValue[0] + "\nY=" + accelerometerValue[1] + "\nZ=" + accelerometerValue[2]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeValue = sensorEvent.values;
            activityMainBinding.tvGyroscope.setText("GYROSCOPE: X=" + gyroscopeValue[0] + "\nY=" + gyroscopeValue[1] + "\nZ=" + gyroscopeValue[2]);
        }

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(Observable.interval(1, TimeUnit.MINUTES).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sensorDatabase.sensorDAO().insert(new SensorData(new Date().getTime(), proximityValue, lightSensorValue, accelerometerValue[0], accelerometerValue[1], accelerometerValue[2], gyroscopeValue[0], gyroscopeValue[1], gyroscopeValue[2])).subscribe();
                    }
                }, TimeUnit.MINUTES.toMillis(4));
            }

            @Override
            public void onError(Throwable e) {
                // Handle the error
            }

            @Override
            public void onComplete() {
                if (!compositeDisposable.isDisposed()) {
                    compositeDisposable.dispose();
                }
            }
        }));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, (int) TimeUnit.MINUTES.toMicros(5));
        }

        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, (int) TimeUnit.MINUTES.toMicros(5));
        }

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, (int) TimeUnit.MINUTES.toMicros(5));
        }

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, (int) TimeUnit.MINUTES.toMicros(5));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}