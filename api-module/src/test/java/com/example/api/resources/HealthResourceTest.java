package com.example.api.resources;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HealthResourceTest {

    private final HealthResource healthResource = new HealthResource();

    @Test
    void health_shouldReturnStatusUp() {
        Map<String, String> result = healthResource.health();

        assertNotNull(result);
        assertEquals("UP", result.get("status"));
        assertNotNull(result.get("timestamp"));
    }
}
