package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

// Room represents a physical room in the campus
// This is a POJO (Plain Old Java Object) — just fields, getters and setters
public class Room {

    // Unique identifier for the room e.g. "LIB-301"
    private String id;

    // Human readable name e.g. "Library Quiet Study"
    private String name;

    // Maximum number of people allowed in the room
    private int capacity;

    // List of sensor IDs that are installed in this room
    // We store only the IDs (not full sensor objects) to avoid circular references
    private List<String> sensorIds = new ArrayList<>();

    // --- Constructors ---

    // Empty constructor — required by Jackson to convert JSON to Java object
    public Room() {}

    // Full constructor — useful when creating a room manually in code
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    // --- Getters (read the value of a field) ---

    public String getId() { return id; }

    public String getName() { return name; }

    public int getCapacity() { return capacity; }

    public List<String> getSensorIds() { return sensorIds; }

    // --- Setters (change the value of a field) ---

    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setCapacity(int capacity) { this.capacity = capacity; }

    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }
}