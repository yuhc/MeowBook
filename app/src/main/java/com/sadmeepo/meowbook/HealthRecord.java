package com.sadmeepo.meowbook;

import java.time.Instant;

public class HealthRecord {
    private Instant time;
    private double weightInKg;

    public String getTime() {
        return time.toString();
    }

    public double getWeightInKg() {
        return weightInKg;
    }

    public double getWeightInLbs(){
        return weightInKg * 2.20462262185;
    }

    public HealthRecord(double weightInKg) {
        // https://stackoverflow.com/questions/308683/how-can-i-get-the-current-date-and-time-in-utc-or-gmt-in-java
        this.time = Instant.now();
        this.weightInKg = weightInKg;
    }
}
