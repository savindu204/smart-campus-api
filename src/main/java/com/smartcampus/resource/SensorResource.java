package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// This class handles all requests to /api/v1/sensors
@Path("/sensors")
public class SensorResource {

    private static final Logger LOGGER = Logger.getLogger(SensorResource.class.getName());

    // GET /api/v1/sensors
    // GET /api/v1/sensors?type=CO2  (optional filter by type)
    // @QueryParam means the client can add ?type=something to the URL
    // If no type is provided, it returns ALL sensors
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {

        LOGGER.info("[SENSOR] GET /sensors" + (type != null ? "?type=" + type : ""));

        List<Sensor> result = new ArrayList<>(DataStore.sensors.values());

        // If a type filter was provided, filter the list
        // e.g. ?type=CO2 returns only CO2 sensors
        if (type != null && !type.isBlank()) {
            result.removeIf(sensor ->
                    !sensor.getType().equalsIgnoreCase(type)
            );
        }

        return Response
                .status(Response.Status.OK)  // 200
                .entity(result)
                .build();
    }

    // GET /api/v1/sensors/{sensorId}
    // Returns a single sensor by its ID
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {

        LOGGER.info("[SENSOR] GET /sensors/" + sensorId);

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

        return Response
                .status(Response.Status.OK)
                .entity(sensor)
                .build();
    }

    // POST /api/v1/sensors
    // Registers a new sensor
    // IMPORTANT: validates that the roomId in the request actually exists
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {

        LOGGER.info("[SENSOR] POST /sensors - registering sensor: " + sensor.getId());

        // Validate required fields
        if (sensor.getId() == null || sensor.getId().isBlank()) {
            ErrorResponse error = new ErrorResponse(
                    400, "Bad Request", "Sensor 'id' field is required."
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Check if sensor ID already exists
        if (DataStore.sensors.containsKey(sensor.getId())) {
            ErrorResponse error = new ErrorResponse(
                    409, "Conflict",
                    "Sensor with ID '" + sensor.getId() + "' already exists."
            );
            return Response.status(Response.Status.CONFLICT)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // CRITICAL validation: check that the roomId actually exists
        // If the room doesn't exist, throw LinkedResourceNotFoundException
        // This will be caught by LinkedResourceNotFoundMapper → returns 422
        if (sensor.getRoomId() == null ||
                !DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }

        // Save the sensor to our store
        DataStore.sensors.put(sensor.getId(), sensor);

        // Also initialize an empty readings list for this sensor
        DataStore.initReadingsForSensor(sensor.getId());

        // Add this sensor's ID to the room's sensorIds list
        // This keeps the room and sensor data in sync
        DataStore.rooms.get(sensor.getRoomId())
                .getSensorIds().add(sensor.getId());

        LOGGER.info("[SENSOR] Sensor created successfully: " + sensor.getId()
                + " in room: " + sensor.getRoomId());

        return Response
                .status(Response.Status.CREATED)  // 201
                .entity(sensor)
                .build();
    }

    // DELETE /api/v1/sensors/{sensorId}
    // Removes a sensor and cleans up its reference from the parent room
    @DELETE
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {

        LOGGER.info("[SENSOR] DELETE /sensors/" + sensorId);

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

        // Remove sensor ID from its parent room's sensorIds list
        // This keeps the room data clean and consistent
        String roomId = sensor.getRoomId();
        if (roomId != null && DataStore.rooms.containsKey(roomId)) {
            DataStore.rooms.get(roomId).getSensorIds().remove(sensorId);
        }

        // Remove the sensor and all its readings from the store
        DataStore.sensors.remove(sensorId);
        DataStore.readings.remove(sensorId);

        LOGGER.info("[SENSOR] Sensor deleted: " + sensorId);

        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Sensor '" + sensorId + "' deleted successfully.");
        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // SUB-RESOURCE LOCATOR — this is the key pattern for Part 4 (20 marks)
    // When a request comes in for /api/v1/sensors/{sensorId}/readings
    // JAX-RS calls this method first, which hands off control to SensorReadingResource
    // Notice: NO @GET, @POST etc. — just @Path
    // This is what makes it a "locator" not a regular endpoint
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(
            @PathParam("sensorId") String sensorId) {

        LOGGER.info("[SENSOR] Delegating to SensorReadingResource for sensor: "
                + sensorId);

        // Create and return a new instance of SensorReadingResource
        // passing the sensorId so it knows which sensor's readings to manage
        return new SensorReadingResource(sensorId);
    }
}