package com.sadmeepo.meowbook.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HealthRecord.class}, version = 1)
public abstract class HealthRecordDatabase extends RoomDatabase {
    private static final String DB_NAME = "health_record_db";
    private static HealthRecordDatabase instance;

    public static synchronized HealthRecordDatabase getInstance(Context context) {
        // TODO: Do not allow on main thread.
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    HealthRecordDatabase.class,
                    DB_NAME).fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract HealthRecordDao healthRecordDao();
}
