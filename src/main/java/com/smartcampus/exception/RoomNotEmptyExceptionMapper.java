package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PART 5.1 - RoomNotEmptyException -> HTTP 409 Conflict
 *
 * Returns a structured JSON error when attempting to delete a room that still has active sensors.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 409);
        error.put("error", "Conflict");
        error.put("message", "Room '" + ex.getRoomId() + "' cannot be deleted because it contains "
                + ex.getSensorCount() + " active sensor(s). "
                + "Please decommission or reassign all sensors before deleting the room.");
        error.put("roomId", ex.getRoomId());
        error.put("activeSensors", ex.getSensorCount());
        error.put("hint", "DELETE /api/v1/sensors/{sensorId} to remove sensors first");

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
