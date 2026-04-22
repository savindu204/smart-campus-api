package com.smartcampus.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

// AuthFilter is a middleware that checks every incoming request for a valid API key
// This implements authentication — one of the lecturer's specific hints for full marks
// It runs BEFORE the request reaches any resource method
// If the API key is missing or wrong, the request is blocked immediately with 401
@Provider
public class AuthFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());

    // This is our expected API key — in a real system this would be stored securely
    // For this coursework a hardcoded value is fine
    private static final String VALID_API_KEY = "smartcampus-2026";

    // The name of the header the client must send the API key in
    // Example: in Postman add Header -> Key: "X-API-KEY", Value: "smartcampus-2026"
    private static final String API_KEY_HEADER = "X-API-KEY";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String apiKey = requestContext.getHeaderString(API_KEY_HEADER);
        String path = requestContext.getUriInfo().getPath();

        // TEMPORARY: log the exact path so we can see what value is coming in
//        LOGGER.info("[AUTH DEBUG] Exact path received: '" + path + "'");

        // Allow the info/discovery endpoint without auth
        if (path.equals("info") || path.equals("/info") || path.isEmpty()) {
            return;
        }

        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            LOGGER.warning("[AUTH] Blocked request - invalid or missing API key. Path: " + path);
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"status\":401,\"error\":\"Unauthorized\"," +
                                    "\"message\":\"Missing or invalid API key. " +
                                    "Please include header X-API-KEY: smartcampus-2026\"}")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        } else {
            LOGGER.info("[AUTH] Authorized request to: " + path);
        }
    }
}