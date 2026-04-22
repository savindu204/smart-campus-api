package com.smartcampus.exception;

// This exception is thrown when someone tries to POST a new reading
// to a sensor that is currently in "MAINTENANCE" status
// It will be caught by SensorUnavailableMapper and returned as HTTP 403 Forbidden
public class SensorUnavailableException extends RuntimeException {

    private final String sensorId;
    private final String status;

    public SensorUnavailableException(String sensorId, String status) {
        super("Sensor '" + sensorId + "' is currently '" + status + "' and cannot accept new readings.");
        this.sensorId = sensorId;
        this.status = status;
    }

    public String getSensorId() { return sensorId; }

    public String getStatus() { return status; }
}