package com.smartcampus.exception;

// This exception is thrown when someone tries to POST a new Sensor
// with a roomId that does not exist in our DataStore
// It will be caught by LinkedResourceNotFoundMapper and returned as HTTP 422
public class LinkedResourceNotFoundException extends RuntimeException {

    private final String resourceType; // e.g. "Room"
    private final String resourceId;   // e.g. "LIB-999"

    public LinkedResourceNotFoundException(String resourceType, String resourceId) {
        super("Cannot complete request: " + resourceType + " with ID '" + resourceId + "' does not exist.");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() { return resourceType; }

    public String getResourceId() { return resourceId; }
}