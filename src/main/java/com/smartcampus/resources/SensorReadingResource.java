package com.smartcampus.resources;

import com.smartcampus.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PART 4 - SensorReadingResource (Sub-Resource)
 *
 * This is the result class of the sub-resource locator pattern.
 * Delegated from SensorResource.
 *
 * GET  /api/v1/sensors/{sensorId}/readings -> Reading history
 * POST /api/v1/sensors/{sensorId}/readings -> Add new reading
 *
 * No @Path annotation here - the path is defined in the parent locator
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * PART 4.2 - GET reading history for a sensor
     */
    @GET
    public Response getReadings() {
        List<SensorReading> readings = store.getReadingsForSensor(sensorId);
        Sensor sensor = store.getSensorById(sensorId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sensorId", sensorId);
        response.put("sensorType", sensor.getType());
        response.put("currentValue", sensor.getCurrentValue());
        response.put("totalReadings", readings.size());
        response.put("readings", readings);

        return Response.ok(response).build();
    }

    /**
     * PART 4.2 - POST new reading for a sensor
     *
     * Side effect: Updates the parent sensor's currentValue (data consistency)
     *
     * PART 5.3 - MAINTENANCE sensor -> throws 403 Forbidden
     */
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensorById(sensorId);

        // PART 5.3 - State Constraint: Cannot POST to a MAINTENANCE sensor
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // OFFLINE sensor also cannot accept readings
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        // Validate reading value
        if (reading == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Bad Request");
            error.put("message", "Reading body is required with a 'value' field");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // Create proper reading with timestamp and UUID
        SensorReading newReading = new SensorReading(reading.getValue());

        // Store reading
        store.addReading(sensorId, newReading);

        // PART 4.2 - Side effect: Update parent sensor's currentValue
        sensor.setCurrentValue(newReading.getValue());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Reading recorded successfully");
        response.put("sensorId", sensorId);
        response.put("reading", newReading);
        response.put("updatedSensorValue", sensor.getCurrentValue());

        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
