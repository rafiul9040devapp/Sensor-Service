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
import com.rafiul.sensorservice.series.BarChart;
import com.rafiul.sensorservice.series.LinearChart;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor lightSensor, proximitySensor, accelerometerSensor, gyroscopeSensor;
    float proximityValue, lightSensorValue;
    float[] accelerometerValue, gyroscopeValue;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SensorDatabase sensorDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("Sensor Service");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        registerSensorListener(proximitySensor);
        registerSensorListener(accelerometerSensor);
        registerSensorListener(lightSensor);
        registerSensorListener(gyroscopeSensor);

        sensorDatabase = SensorDatabase.getSensorDataBase(getApplicationContext());
        setCardClickListeners();

    }

    private void registerSensorListener(Sensor sensor) {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, (int) TimeUnit.MINUTES.toMicros(5));
        }
    }

    private void setCardClickListeners() {
        binding.cardProximity.setOnClickListener(v -> startChartActivity("Proximity"));
        binding.cardLight.setOnClickListener(v -> startChartActivity("Light"));
        binding.cardAccelerometer.setOnClickListener(v -> startChartActivity("Accelerometer"));
        binding.cardGyroscope.setOnClickListener(v -> startChartActivity("Gyroscope"));
    }

    private void startChartActivity(String sensorType) {
        Intent intent = new Intent(this, sensorType.equals("Accelerometer") ? BarChart.class : LinearChart.class);
        intent.putExtra("Sensor", sensorType);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_PROXIMITY -> {
                proximityValue = sensorEvent.values[0];
                binding.tvProximitySensor.setText("PROXIMITY SENSOR: " + proximityValue);
            }
            case Sensor.TYPE_LIGHT -> {
                lightSensorValue = sensorEvent.values[0];
                binding.tvLightSensor.setText("LIGHT SENSOR: " + lightSensorValue);
            }
            case Sensor.TYPE_ACCELEROMETER -> {
                accelerometerValue = sensorEvent.values;
                binding.tvAccelerometer.setText("ACCELEROMETER:" + "\nX=" + accelerometerValue[0] + "\nY=" + accelerometerValue[1] + "\nZ=" + accelerometerValue[2]);
            }
            case Sensor.TYPE_GYROSCOPE -> {
                gyroscopeValue = sensorEvent.values;
                binding.tvGyroscope.setText("GYROSCOPE:" + "\nX=" + gyroscopeValue[0] + "\nY=" + gyroscopeValue[1] + "\nZ=" + gyroscopeValue[2]);
            }
        }

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                Observable.interval(1, TimeUnit.MINUTES)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<Long>() {
                            @Override
                            public void onNext(Long aLong) {
                                handler.postDelayed(() -> {
                                    sensorDatabase.sensorDAO().insert(
                                            new SensorData(
                                                    new Date().getTime(),
                                                    proximityValue,
                                                    lightSensorValue,
                                                    accelerometerValue[0],
                                                    accelerometerValue[1],
                                                    accelerometerValue[2],
                                                    gyroscopeValue[0],
                                                    gyroscopeValue[1],
                                                    gyroscopeValue[2]
                                            )
                                    ).subscribe();
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
                        })
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListener(proximitySensor);
        registerSensorListener(accelerometerSensor);
        registerSensorListener(lightSensor);
        registerSensorListener(gyroscopeSensor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}