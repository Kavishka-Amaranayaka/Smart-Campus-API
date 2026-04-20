package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * PART 5.5 - API Request & Response Logging Filter
 *
 * ContainerRequestFilter  -> Logs incoming requests (HTTP method + URI)
 * ContainerResponseFilter -> Logs outgoing responses (HTTP status code)
 *
 * This is a cross-cutting concern - instead of adding Logger.info() in every resource method,
 * using a filter is better:
 * 1. No code duplication - managed in a single place
 * 2. Resource methods stay clean (Single Responsibility Principle)
 * 3. Easy to add/remove the filter - no resource code changes needed
 * 4. AOP (Aspect-Oriented Programming) principle - separates concerns
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Logs incoming requests
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info(String.format("[REQUEST]  Method: %-7s | URI: %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()));
    }

    /**
     * Logs outgoing responses
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info(String.format("[RESPONSE] Method: %-7s | URI: %s | Status: %d %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus(),
                responseContext.getStatusInfo().getReasonPhrase()));
    }
}
