package com.smartcampus.exception;

// This exception is thrown when someone tries to DELETE a room
// that still has sensors assigned to it
// It will be caught by RoomNotEmptyMapper and returned as HTTP 409 Conflict
public class RoomNotEmptyException extends RuntimeException {

    private final String roomId;

    // Constructor — takes the room ID so we can mention it in the error message
    public RoomNotEmptyException(String roomId) {
        super("Room '" + roomId + "' cannot be deleted because it still has sensors assigned to it.");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}