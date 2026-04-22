package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

// This class handles all requests to /api/v1/rooms
@Path("/rooms")
public class RoomResource {

    private static final Logger LOGGER = Logger.getLogger(RoomResource.class.getName());

    // GET /api/v1/rooms
    // Returns a list of ALL rooms currently stored
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {

        LOGGER.info("[ROOM] GET /rooms - fetching all rooms");

        // Convert the map values to a list and return it
        List<Room> roomList = new ArrayList<>(DataStore.rooms.values());

        return Response
                .status(Response.Status.OK)  // 200
                .entity(roomList)
                .build();
    }

    // GET /api/v1/rooms/{roomId}
    // Returns a single room by its ID
    // {roomId} is a path variable — e.g. GET /api/v1/rooms/LIB-301
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {

        LOGGER.info("[ROOM] GET /rooms/" + roomId);

        Room room = DataStore.rooms.get(roomId);

        // If no room found with that ID, return 404 Not Found
        if (room == null) {
            ErrorResponse error = new ErrorResponse(
                    404, "Not Found",
                    "Room with ID '" + roomId + "' does not exist."
            );
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response
                .status(Response.Status.OK)  // 200
                .entity(room)
                .build();
    }

    // POST /api/v1/rooms
    // Creates a new room from the JSON body sent by the client
    // @Consumes means "this method expects JSON input"
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {

        LOGGER.info("[ROOM] POST /rooms - creating room: " + room.getId());

        // Check if a room with this ID already exists
        if (DataStore.rooms.containsKey(room.getId())) {
            ErrorResponse error = new ErrorResponse(
                    409, "Conflict",
                    "Room with ID '" + room.getId() + "' already exists."
            );
            return Response.status(Response.Status.CONFLICT)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Validate that required fields are present
        if (room.getId() == null || room.getId().isBlank()) {
            ErrorResponse error = new ErrorResponse(
                    400, "Bad Request", "Room 'id' field is required."
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Save the room to our in-memory store
        DataStore.rooms.put(room.getId(), room);

        LOGGER.info("[ROOM] Room created successfully: " + room.getId());

        // Return 201 Created with the newly created room as the body
        // This tells the client exactly what was saved
        return Response
                .status(Response.Status.CREATED)  // 201
                .entity(room)
                .build();
    }

    // DELETE /api/v1/rooms/{roomId}
    // Deletes a room — but ONLY if it has no sensors assigned
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        LOGGER.info("[ROOM] DELETE /rooms/" + roomId);

        Room room = DataStore.rooms.get(roomId);

        // If room doesn't exist return 404
        // DELETE is idempotent — calling it again on a deleted room also returns 404
        // The server state doesn't change either way so this is still idempotent
        if (room == null) {
            ErrorResponse error = new ErrorResponse(
                    404, "Not Found",
                    "Room with ID '" + roomId + "' does not exist."
            );
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Business rule: cannot delete a room that still has sensors
        // This prevents orphaned sensors (sensors with no valid room)
        if (!room.getSensorIds().isEmpty()) {
            // Throw our custom exception — RoomNotEmptyMapper will catch it
            // and return a proper 409 JSON response automatically
            throw new RoomNotEmptyException(roomId);
        }

        // Safe to delete — remove from store
        DataStore.rooms.remove(roomId);

        LOGGER.info("[ROOM] Room deleted successfully: " + roomId);

        // Return 200 OK with a confirmation message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Room '" + roomId + "' deleted successfully.");
        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}