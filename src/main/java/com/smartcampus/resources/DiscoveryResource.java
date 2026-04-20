package com.smartcampus.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PART 1.2 - Discovery Endpoint
 * GET /api/v1 -> API metadata, version, links (HATEOAS)
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        Map<String, Object> response = new LinkedHashMap<>();

        // API versioning info
        response.put("API Name", "Smart Campus Sensor & Room Management API");
        response.put("Version", "1.0.0");
        response.put("Description", "RESTful API for managing campus rooms and IoT sensors");

        // Admin contact
        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("Name", "Smart Campus Admin");
        contact.put("Email", "admin@smartcampus.ac.lk");
        response.put("Contact", contact);

        // HATEOAS - Resource links (primary navigation map)
        // This makes it easy for client developers to navigate the API
        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("Rooms", "/api/v1/rooms");
        resources.put("Sensors", "/api/v1/sensors");
        resources.put("Sensor Readings Example", "/api/v1/sensors/{sensorId}/readings");
        response.put("Resources", resources);

        // Additional HATEOAS links
        Map<String, String> links = new LinkedHashMap<>();
        links.put("Main Link", "/api/v1");
        links.put("Rooms", "/api/v1/rooms");
        links.put("Sensors", "/api/v1/sensors");
        response.put("Links", links);

        return Response.ok(response).build();
    }
}
