package com.sadmeepo.meowbook.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HealthRecordDao {
    @Query("SELECT * FROM health_records")
    List<HealthRecord> getAll();

    @Query("SELECT * FROM health_records WHERE pet_name LIKE :name")
    List<HealthRecord> loadAllByName(String name);

    @Query("SELECT * FROM health_records WHERE pet_name LIKE :name LIMIT 1")
    HealthRecord findByName(String name);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(HealthRecord healthRecord);

    @Update
    void update(HealthRecord healthRecord);

    @Delete
    void delete(HealthRecord healthRecord);

    @Query("DELETE FROM health_records")
    public void nukeTable();
}
