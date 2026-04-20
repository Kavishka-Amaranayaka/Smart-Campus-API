package com.smartcampus.resources;

import com.smartcampus.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PART 3 - Sensor Operations
 *
 * GET    /api/v1/sensors              -> List all sensors
 * POST   /api/v1/sensors              -> Register new sensor (with roomId validation)
 * GET    /api/v1/sensors/{sensorId}   -> Get specific sensor
 * DELETE /api/v1/sensors/{sensorId}   -> Remove sensor
 *
 * PART 4 (Sub-Resource Locator):
 * GET/POST /api/v1/sensors/{sensorId}/readings -> Delegate to SensorReadingResource
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    /**
     * PART 3.2 - GET all sensors with optional type filter
     * ?type=CO2 -> only CO2 sensors
     * Uses query param (not path param) - suitable for filtering/searching
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(store.getSensors().values());

        // Filter by type if provided (case-insensitive)
        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type.trim()))
                    .collect(Collectors.toList());
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("count", sensorList.size());
        if (type != null && !type.trim().isEmpty()) {
            response.put("filter", "type=" + type);
        }
        response.put("sensors", sensorList);

        return Response.ok(response).build();
    }

    /**
     * PART 3.1 - POST register new sensor
     * roomId validation - sensor is only added if the referenced room exists
     */
    @POST
    public Response createSensor(Sensor sensor) {
        // Basic validation
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Sensor ID is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "roomId is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // PART 3.1 - roomId existence check -> throws 422 if not found
        if (store.getRoomById(sensor.getRoomId()) == null) {
            throw new LinkedResourceNotFoundException("roomId", sensor.getRoomId());
        }

        // Duplicate sensor check
        if (store.getSensorById(sensor.getId()) != null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Conflict");
            error.put("message", "Sensor with ID '" + sensor.getId() + "' already exists");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        // Default status
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        // Add sensor to store
        store.addSensor(sensor);

        // Link sensor to room
        store.getRoomById(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Sensor registered successfully");
        response.put("sensor", sensor);

        return Response.status(Response.Status.CREATED)
                .header("Location", "/api/v1/sensors/" + sensor.getId())
                .entity(response)
                .build();
    }

    /**
     * GET sensor by ID
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensorById(sensorId);

        if (sensor == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Sensor '" + sensorId + "' not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(sensor).build();
    }

    /**
     * DELETE sensor - removes from the room's sensorIds list
     */
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensorById(sensorId);

        if (sensor == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Not Found");
            error.put("message", "Sensor '" + sensorId + "' not found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // Remove from parent room's sensor list
        String roomId = sensor.getRoomId();
        if (roomId != null && store.getRoomById(roomId) != null) {
            store.getRoomById(roomId).getSensorIds().remove(sensorId);
        }

        store.getSensors().remove(sensorId);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Sensor '" + sensorId + "' removed successfully");

        return Response.ok(response).build();
    }

    /**
     * PART 4 - Sub-Resource Locator
     * /sensors/{sensorId}/readings -> Delegates to the SensorReadingResource class
     *
     * This uses the sub-resource locator pattern.
     * Benefit: Makes complexity easier to manage - not every nested path is in the same class.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensorById(sensorId);

        if (sensor == null) {
            // If sub-resource locator returns null, JAX-RS throws 404
            // However, we explicitly throw a proper exception when sensor is null
            throw new javax.ws.rs.NotFoundException("Sensor '" + sensorId + "' not found");
        }

        return new SensorReadingResource(sensorId);
    }
}
