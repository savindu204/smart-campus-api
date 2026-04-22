package com.smartcampus.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

// @Provider tells JAX-RS to auto-detect this filter
// ContainerRequestFilter  = runs BEFORE the request reaches our resource methods
// ContainerResponseFilter = runs AFTER our resource methods send back a response
// The lecturer specifically said: log a message BEFORE and AFTER every request
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // Create a logger specifically for this filter class
    // This is the correct way — never use System.out.println (lecturer requirement)
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    // This method runs BEFORE every request hits our API
    // ContainerRequestContext gives us info about the incoming request
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Log the HTTP method (GET, POST, DELETE) and the full URL being called
        // Example output: "[REQUEST] POST http://localhost:8080/api/v1/rooms"
        LOGGER.info("[REQUEST] "
                + requestContext.getMethod()          // e.g. GET, POST, DELETE
                + " "
                + requestContext.getUriInfo().getRequestUri()
                +"\n"); // full URL
    }

    // This method runs AFTER every response is sent back to the client
    // ContainerResponseContext gives us info about the outgoing response
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Log the HTTP method, URL, and the status code that was returned
        // Example output: "[RESPONSE] POST http://localhost:8080/api/v1/rooms -> 201"
        LOGGER.info("[RESPONSE] "
                + requestContext.getMethod()
                + " "
                + requestContext.getUriInfo().getRequestUri()
                + " -> "
                + responseContext.getStatus() // like ( 200, 201, 404, 409 )
                +"\n"
                +"---");
    }
}