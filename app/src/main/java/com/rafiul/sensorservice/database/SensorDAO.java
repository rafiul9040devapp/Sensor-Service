package com.rafiul.sensorservice.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;



import java.util.List;

import io.reactivex.Single;

@Dao
public interface SensorDAO {
    @Insert
    Single<Long> insert(SensorData sensorData);

    @Query("SELECT * FROM sensor_data")
    List<SensorData> getAllSensorData();

}
