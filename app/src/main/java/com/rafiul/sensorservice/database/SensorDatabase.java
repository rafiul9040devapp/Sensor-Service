package com.rafiul.sensorservice.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SensorData.class},version = 1, exportSchema = false)
public abstract class SensorDatabase extends RoomDatabase {
    public abstract SensorDAO sensorDAO();


    private static SensorDatabase sensorDatabase = null;

    public static SensorDatabase getSensorDataBase(Context context) {

        if (sensorDatabase == null) {
            sensorDatabase = Room.databaseBuilder(context,SensorDatabase.class,"sensor_database").allowMainThreadQueries().build();
        }
        return sensorDatabase;
    }

}
