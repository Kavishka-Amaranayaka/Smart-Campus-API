package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PART 5.4 - Global Safety Net ExceptionMapper
 *
 * Prevents Java stack traces from being exposed - CRITICAL security requirement.
 * Catches NullPointerException, IndexOutOfBoundsException etc.
 *
 * Stack traces expose:
 * 1. Internal file paths
 * 2. Library versions (Jersey 2.39, Jackson 2.15 etc.) - known CVE targets
 * 3. Internal class/method names - reveals logic flaws
 * 4. Database query structures (SQL errors)
 * Therefore, NEVER expose stack traces to external API consumers.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        // Log internally (server side only - not visible to client)
        LOGGER.log(Level.SEVERE, "Unexpected error occurred: " + ex.getMessage(), ex);

        // Send a generic, safe response to the client
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred on the server. "
                + "Please contact the API administrator if the problem persists.");
        error.put("support", "admin@smartcampus.ac.uk");

        // IMPORTANT: NEVER include stack traces or internal details in the response
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
