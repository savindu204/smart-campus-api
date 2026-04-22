package com.smartcampus.model;

// Sensor represents a physical sensor installed in a room
public class Sensor {

    // Unique identifier e.g. "TEMP-001"
    private String id;

    // Category of sensor e.g. "Temperature", "CO2", "Occupancy"
    private String type;

    // Current state of the sensor — must be "ACTIVE", "MAINTENANCE" or "OFFLINE"
    private String status;

    // The most recent measurement recorded by this sensor
    private double currentValue;

    // The ID of the room this sensor belongs to (links Sensor to Room)
    private String roomId;

    // --- Constructors ---

    // Empty constructor — required by Jackson
    public Sensor() {}

    // Full constructor
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    // --- Getters ---

    public String getId() { return id; }

    public String getType() { return type; }

    public String getStatus() { return status; }

    public double getCurrentValue() { return currentValue; }

    public String getRoomId() { return roomId; }

    // --- Setters ---

    public void setId(String id) { this.id = id; }

    public void setType(String type) { this.type = type; }

    public void setStatus(String status) { this.status = status; }

    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
}