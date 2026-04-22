package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

// This class is NOT registered directly with JAX-RS
// It is only ever created by SensorResource's sub-resource locator method
// That's what makes this the "Sub-Resource" pattern (Part 4 - 20 marks)
// It handles all requests to /api/v1/sensors/{sensorId}/readings
public class SensorReadingResource {

    private static final Logger LOGGER = Logger.getLogger(SensorReadingResource.class.getName());

    // The sensor ID passed in from SensorResource's locator method
    // This tells us WHICH sensor's readings we are working with
    private final String sensorId;

    // Constructor — called by SensorResource.getReadingResource()
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    // Returns the full reading history for this sensor
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReadings() {

        LOGGER.info("[READING] GET /sensors/" + sensorId + "/readings");

        // First check if the sensor actually exists
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            ErrorResponse error = new ErrorResponse(
                    404, "Not Found",
                    "Sensor with ID '" + sensorId + "' does not exist."
            );
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Get all readings for this sensor from the store
        List<SensorReading> readingList = DataStore.getReadingsForSensor(sensorId);

        return Response
                .status(Response.Status.OK)  // 200
                .entity(readingList)
                .build();
    }

    // POST /api/v1/sensors/{sensorId}/readings
    // Records a new measurement for this sensor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {

        LOGGER.info("[READING] POST /sensors/" + sensorId + "/readings");

        // Check the sensor exists
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            ErrorResponse error = new ErrorResponse(
                    404, "Not Found",
                    "Sensor with ID '" + sensorId + "' does not exist."
            );
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // IMPORTANT: if sensor is in MAINTENANCE status, reject the reading
        // Throw SensorUnavailableException — SensorUnavailableMapper catches it
        // and returns HTTP 403 Forbidden automatically
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // Also reject if sensor is OFFLINE
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // Auto-generate ID and timestamp if not provided by client
        // Using UUID so every reading gets a guaranteed unique identifier
        reading.setId(java.util.UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());

        // Safety check — ensure readings list exists for this sensor
        DataStore.initReadingsForSensor(sensorId);

        // Save the reading to the store
        DataStore.readings.get(sensorId).add(reading);

        // SIDE EFFECT (required by Part 4.2):
        // Update the parent sensor's currentValue with this new reading's value
        // This keeps the sensor's "latest value" always up to date
        sensor.setCurrentValue(reading.getValue());

        LOGGER.info("[READING] New reading saved for sensor: " + sensorId
                + " value: " + reading.getValue()
                + " | Sensor currentValue updated to: " + reading.getValue());

        return Response
                .status(Response.Status.CREATED)  // 201
                .entity(reading)
                .build();
    }
}