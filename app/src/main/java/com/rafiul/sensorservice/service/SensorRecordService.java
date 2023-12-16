package com.rafiul.sensorservice.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import com.rafiul.sensorservice.MainActivity;
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
    private final CompositeDisposable disposables = new CompositeDisposable();

    private SensorManager sensorManager;
    private Sensor lightSensor, proximitySensor, accelerometerSensor, gyroscopeSensor;
    private float proximityValue, lightSensorValue;
    private float[] accelerometerValue, gyroscopeValue;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            disposables.add(
                    Observable.fromCallable(() -> recordSensorData())
                            .subscribeOn(Schedulers.io())
                            .subscribe()
            );
            handler.postDelayed(this, TimeUnit.MINUTES.toMillis(5));
        }
    };

    private final Runnable notificationRunnable = new Runnable() {
        @Override
        public void run() {
            updateNotification();
            handler.postDelayed(this, TimeUnit.MINUTES.toMillis(1));
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        handler.postDelayed(notificationRunnable, TimeUnit.MINUTES.toMillis(1));
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
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_PROXIMITY -> proximityValue = sensorEvent.values[0];
            case Sensor.TYPE_LIGHT -> lightSensorValue = sensorEvent.values[0];
            case Sensor.TYPE_ACCELEROMETER -> accelerometerValue = sensorEvent.values;
            case Sensor.TYPE_GYROSCOPE -> gyroscopeValue = sensorEvent.values;
        }
        recordSensorData();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startSensorListener() {
        registerSensorListener(proximitySensor);
        registerSensorListener(accelerometerSensor);
        registerSensorListener(lightSensor);
        registerSensorListener(gyroscopeSensor);
    }

    private void registerSensorListener(Sensor sensor) {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Sensor Service")
                .setContentText("Running in the background")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

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

    private void updateNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, createNotification());
    }

    private boolean recordSensorData() {
        disposables.add(
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
                ).subscribe()
        );
        return true;
    }
}
