package com.rafiul.sensorservice.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sensor_data")
public class SensorData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "time")
    private long timeStamp;
    @ColumnInfo(name = "proximity")
    private float proximity;
    @ColumnInfo(name = "light")
    private float light;
    @ColumnInfo(name = "accelerometerX")
    private float accelerometerX;
    @ColumnInfo(name = "accelerometerY")
    private float accelerometerY;
    @ColumnInfo(name = "accelerometerZ")
    private float accelerometerZ;
    @ColumnInfo(name = "gyroscopeX")
    private float gyroscopeX;
    @ColumnInfo(name = "gyroscopeY")
    private float gyroscopeY;
    @ColumnInfo(name = "gyroscopeZ")
    private float gyroscopeZ;

    public SensorData(long timeStamp, float proximity, float light, float accelerometerX, float accelerometerY, float accelerometerZ, float gyroscopeX, float gyroscopeY, float gyroscopeZ) {
        this.timeStamp = timeStamp;
        this.proximity = proximity;
        this.light = light;
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
        this.gyroscopeX = gyroscopeX;
        this.gyroscopeY = gyroscopeY;
        this.gyroscopeZ = gyroscopeZ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public float getProximity() {
        return proximity;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public float getAccelerometerX() {
        return accelerometerX;
    }

    public void setAccelerometerX(float accelerometerX) {
        this.accelerometerX = accelerometerX;
    }

    public float getAccelerometerY() {
        return accelerometerY;
    }

    public void setAccelerometerY(float accelerometerY) {
        this.accelerometerY = accelerometerY;
    }

    public float getAccelerometerZ() {
        return accelerometerZ;
    }

    public void setAccelerometerZ(float accelerometerZ) {
        this.accelerometerZ = accelerometerZ;
    }

    public float getGyroscopeX() {
        return gyroscopeX;
    }

    public void setGyroscopeX(float gyroscopeX) {
        this.gyroscopeX = gyroscopeX;
    }

    public float getGyroscopeY() {
        return gyroscopeY;
    }

    public void setGyroscopeY(float gyroscopeY) {
        this.gyroscopeY = gyroscopeY;
    }

    public float getGyroscopeZ() {
        return gyroscopeZ;
    }

    public void setGyroscopeZ(float gyroscopeZ) {
        this.gyroscopeZ = gyroscopeZ;
    }
}
