package com.sadmeepo.meowbook.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_records")
public class HealthRecord {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "pet_name")
    public String name;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "weight_in_kg")
    public double weightInKg;

    public HealthRecord(int uid, String name, String date, double weightInKg) {
        this.uid = uid;
        this.name = name;
        this.date = date;
        this.weightInKg = weightInKg;
    }

    @Ignore
    public HealthRecord(String name, String date, double weightInKg) {
        this.name = name;
        this.date = date;
        this.weightInKg = weightInKg;
    }

    @Override
    public String toString() {
        return "[" + name + "] " + date + ": " + weightInKg + " kg";
    }
}

