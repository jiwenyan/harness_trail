package com.example.api.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查端点
 */
@Path("/api/health")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class HealthResource {

    @GET
    public Map<String, String> health() {
        Map<String, String> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now().toString());
        return healthStatus;
    }
}
