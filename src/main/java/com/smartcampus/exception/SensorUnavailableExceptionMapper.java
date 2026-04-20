package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PART 5.3 - SensorUnavailableException -> HTTP 403 Forbidden
 *
 * Returns 403 Forbidden when attempting to POST a reading to a sensor in
 * MAINTENANCE or OFFLINE status - sensor is physically disconnected.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 403);
        error.put("error", "Forbidden");
        error.put("message", "Sensor '" + ex.getSensorId() + "' is currently in '"
                + ex.getStatus() + "' state. "
                + "Sensors in MAINTENANCE or OFFLINE status cannot accept new readings as they are physically disconnected.");
        error.put("sensorId", ex.getSensorId());
        error.put("currentStatus", ex.getStatus());
        error.put("hint", "Update sensor status to ACTIVE before posting readings: PUT /api/v1/sensors/" + ex.getSensorId());

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
