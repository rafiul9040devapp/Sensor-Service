package com.rafiul.sensorservice.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;


import com.rafiul.sensorservice.R;
import com.rafiul.sensorservice.database.SensorData;
import com.rafiul.sensorservice.database.SensorDatabase;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SensorRecordService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "sensor_channel";
    private static final int NOTIFICATION_ID = 123;

    private SensorDatabase sensorDatabase;
    private CompositeDisposable disposables = new CompositeDisposable();

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Sensor accelerometerSensor;
    private Sensor lightSensor;
    private Sensor gyroscopeSensor;

    float proximityValue, lightSensorValue;
    float[] accelerometerValue, gyroscopeValue;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            disposables.add(
                    Observable.fromCallable(() -> {
//                                recordSensorData();
                                return true;
                            })
                            .subscribeOn(Schedulers.io())
                            .subscribe()
            );
            handler.postDelayed(this, TimeUnit.MINUTES.toMillis(5));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        sensorDatabase = SensorDatabase.getSensorDataBase(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSensorListener();
        handler.post(recordRunnable);
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        recordSensorData(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    private void startSensorListener() {
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void stopSensorListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
        stopSensorListener();
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Sensor Service")
                .setContentText("Running in the background")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sensor Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void recordSensorData(SensorEvent sensorEvent) {
        // Obtain sensor values
        long timestamp = System.currentTimeMillis();

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_PROXIMITY:
                proximityValue = sensorEvent.values[0];
            case Sensor.TYPE_LIGHT:
                lightSensorValue = sensorEvent.values[0];
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValue = sensorEvent.values;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeValue = sensorEvent.values;
        }
        // Insert the sensor data into the database
        disposables.add(
                sensorDatabase.sensorDAO().insert(new SensorData(new Date().getTime(), proximityValue, lightSensorValue, accelerometerValue[0], accelerometerValue[1], accelerometerValue[2], gyroscopeValue[0], gyroscopeValue[1], gyroscopeValue[2])).subscribe()
        );
    }

}
