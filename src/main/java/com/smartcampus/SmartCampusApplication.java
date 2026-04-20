package com.smartcampus;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;


@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        
        // Enable JSON support
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
}
