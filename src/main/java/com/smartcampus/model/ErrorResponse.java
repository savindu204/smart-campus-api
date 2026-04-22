package com.smartcampus.model;

// ErrorResponse is a standard structure we return whenever something goes wrong
// Instead of returning a raw Java error, we return a clean JSON like:
// { "status": 404, "error": "Not Found", "message": "Room LIB-301 does not exist" }
public class ErrorResponse {

    // The HTTP status code number e.g. 404, 409, 500
    private int status;

    // Short name for the error e.g. "Not Found", "Conflict"
    private String error;

    // Detailed human-readable explanation of what went wrong
    private String message;

    // --- Constructor ---

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // --- Getters (setters not needed — this object is only ever read, never modified) ---

    public int getStatus() { return status; }

    public String getError() { return error; }

    public String getMessage() { return message; }
}