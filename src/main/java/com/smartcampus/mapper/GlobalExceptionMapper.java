package com.smartcampus.mapper;

import com.smartcampus.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

// This is the SAFETY NET — it catches absolutely ANY exception that wasn't
// caught by the other mappers (e.g. NullPointerException, ArrayIndexOutOfBoundsException)
// Without this, the server would expose a raw Java stack trace to the client
// which is a serious security risk (attackers can learn about your code structure)
// Throwable is the parent of ALL exceptions in Java — so this catches everything
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // Log the real error internally so we (the developers) can see what happened
        // but we never expose it to the client
        LOGGER.severe("Unexpected error caught by GlobalExceptionMapper: "
                + exception.getClass().getName()
                + " - " + exception.getMessage());

        // Return a generic safe message to the client — no stack trace, no class names
        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later."
        );

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)   // 500
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}