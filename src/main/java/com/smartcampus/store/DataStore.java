package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// DataStore is the "database" — since we are not allowed to use a real database,
// we store everything in memory using Maps (like lookup tables)
// ConcurrentHashMap is used instead of regular HashMap because it is THREAD-SAFE
// This means if two requests arrive at the same time, data won't get corrupted
public class DataStore {

    // Stores all rooms — key is the room ID (e.g. "LIB-301"), value is the Room object
    public static final ConcurrentHashMap<String, Room> rooms
            = new ConcurrentHashMap<>();

    // Stores all sensors — key is sensor ID (e.g. "TEMP-001"), value is the Sensor object
    public static final ConcurrentHashMap<String, Sensor> sensors
            = new ConcurrentHashMap<>();

    // Stores all readings per sensor
    // Key is the sensor ID, value is a list of all readings for that sensor
    public static final ConcurrentHashMap<String, List<SensorReading>> readings
            = new ConcurrentHashMap<>();

    // Private constructor — prevents anyone from creating an instance of DataStore
    // This class is only used statically (DataStore.rooms, DataStore.sensors, etc.)
    private DataStore() {}

    // --- Helper methods ---

    // Adds a new empty readings list for a sensor when it is first registered
    // Called automatically when a new sensor is created
    public static void initReadingsForSensor(String sensorId) {
        // putIfAbsent means: only add if this sensorId doesn't already have a list
        readings.putIfAbsent(sensorId, new ArrayList<>());
    }

    // Returns all readings for a given sensor, or an empty list if none exist
    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        // getOrDefault returns the list if found, or a new empty list if not
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }
}