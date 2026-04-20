package com.smartcampus.model;

import java.util.UUID;

/**
 * SensorReading entity - Represents a reading taken from a sensor.
 * Stored in the historical log.
 */
public class SensorReading {

    private String id;        // UUID
    private long timestamp;   // Epoch milliseconds
    private double value;     // Recorded metric value

    // Default constructor
    public SensorReading() {}

    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
