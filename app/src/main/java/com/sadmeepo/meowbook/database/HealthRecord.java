package com.sadmeepo.meowbook.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_records", primaryKeys = {"pet_name", "date"})
public class HealthRecord {
    @NonNull
    @ColumnInfo(name = "pet_name")
    public String name;

    @NonNull
    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "weight_in_kg")
    public double weightInKg;

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

