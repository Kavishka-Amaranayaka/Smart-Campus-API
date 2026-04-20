package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PART 5.2 - LinkedResourceNotFoundException -> HTTP 422 Unprocessable Entity
 *
 * The "roomId" in the JSON payload is invalid (room does not exist) - returns 422.
 * 422 is more appropriate than 404: Request syntax is correct, but data is semantically invalid.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 422);
        error.put("error", "Unprocessable Entity");
        error.put("message", "The '" + ex.getFieldName() + "' field references a resource that does not exist: '"
                + ex.getFieldValue() + "'. "
                + "Please ensure the referenced resource exists before creating this entity.");
        error.put("field", ex.getFieldName());
        error.put("invalidValue", ex.getFieldValue());
        error.put("hint", "GET /api/v1/rooms to list available rooms");

        return Response.status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
