package com.example.api.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Register Jersey resources and providers here
        packages("com.example.api.resources");
        packages("com.example.api.exception");
    }
}