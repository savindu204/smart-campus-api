package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Catches SensorUnavailableException and returns HTTP 403 Forbidden
// 403 means: "you are not allowed to do this action right now"
// In our case: the sensor is in MAINTENANCE so it cannot accept new readings
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {

        ErrorResponse error = new ErrorResponse(
                403,
                "Forbidden",
                exception.getMessage()
        );

        return Response
                .status(Response.Status.FORBIDDEN)   // 403
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}