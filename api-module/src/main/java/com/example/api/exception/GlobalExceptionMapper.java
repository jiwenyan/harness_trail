package com.example.api.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 全局异常处理
 */
@Provider
@Component
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof IllegalArgumentException) {
            return buildResponse(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Bad Request", exception.getMessage());
        }

        if (exception instanceof IllegalStateException) {
            return buildResponse(Response.Status.CONFLICT.getStatusCode(),
                    "Conflict", exception.getMessage());
        }

        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Internal Server Error", exception.getMessage());
    }

    private Response buildResponse(int status, String error, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                error,
                message,
                LocalDateTime.now().toString()
        );
        return Response.status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
