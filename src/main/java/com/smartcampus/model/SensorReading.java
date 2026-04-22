package com.smartcampus.model;

import java.util.UUID;

// SensorReading represents a single recorded measurement from a sensor
// Every time a sensor records a value, a new SensorReading is created and stored
public class SensorReading {

    // Unique ID for this specific reading event
    // UUID is used because it generates a globally unique string automatically
    private String id;

    // The exact time this reading was recorded
    // Stored as epoch milliseconds (number of ms since Jan 1, 1970)
    private long timestamp;

    // The actual measurement value recorded e.g. 23.5 (degrees), 800 (ppm CO2)
    private double value;

    // --- Constructors ---

    // Empty constructor — required by Jackson
    public SensorReading() {}

    // Constructor that auto-generates ID and timestamp
    // Use this when creating a new reading from a POST request
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString(); // auto generate unique ID
        this.timestamp = System.currentTimeMillis(); // current time in ms
        this.value = value;
    }

    // Full constructor — useful when all fields are known
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // --- Getters ---

    public String getId() { return id; }

    public long getTimestamp() { return timestamp; }

    public double getValue() { return value; }

    // --- Setters ---

    public void setId(String id) { this.id = id; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public void setValue(double value) { this.value = value; }
}