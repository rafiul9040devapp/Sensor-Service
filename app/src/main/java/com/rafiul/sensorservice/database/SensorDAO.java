package com.rafiul.sensorservice.database;

import androidx.room.Dao;
import androidx.room.Insert;

import io.reactivex.Single;

@Dao
public interface SensorDAO {
    @Insert
    Single<Long> insert(SensorData sensorData);
}
