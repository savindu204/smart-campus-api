package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// @Path("/info") means this handles GET /api/v1/info
// We use /info instead of / because JAX-RS has trouble routing to the bare root path
@Path("/info")
public class DiscoveryResource {

    private static final Logger LOGGER = Logger.getLogger(DiscoveryResource.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiInfo() {

        LOGGER.info("[DISCOVERY] GET /api/v1/info - API info requested");

        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("name", "Smart Campus Sensor & Room Management API");
        apiInfo.put("version", "1.0");
        apiInfo.put("description", "A RESTful API to manage campus rooms and sensors");
        apiInfo.put("contact", "admin@smartcampus.ac.uk");

        // HATEOAS links — tell the client where all resources are
        Map<String, String> links = new HashMap<>();
        links.put("info",     "/api/v1/info");
        links.put("rooms",    "/api/v1/rooms");
        links.put("sensors",  "/api/v1/sensors");
        apiInfo.put("resources", links);

        return Response
                .status(Response.Status.OK)
                .entity(apiInfo)
                .build();
    }
}