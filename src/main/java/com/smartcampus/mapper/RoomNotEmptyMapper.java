package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// @Provider tells JAX-RS to automatically detect and register this class
// ExceptionMapper<RoomNotEmptyException> means:
// "whenever a RoomNotEmptyException is thrown anywhere in the app, run this class"
@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {

        // Build a clean JSON error object using our ErrorResponse model
        ErrorResponse error = new ErrorResponse(
                409,                          // HTTP status code
                "Conflict",                   // short error name
                exception.getMessage()        // detailed message from the exception
        );

        // Return HTTP 409 Conflict with the JSON error body
        return Response
                .status(Response.Status.CONFLICT)   // 409
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}