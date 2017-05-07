package com.pollaroid.resources;

import lombok.AllArgsConstructor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A JSON container for Error messages.
 */
@AllArgsConstructor
class ErrorResponse {
    public String message;
}

/**
 * Represents an error that will be returned as a JSON object.
 */
public class PollaroidError extends WebApplicationException {

    public PollaroidError(String message) {
        this(message, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public PollaroidError(String message, Response.Status status) {
        super(Response.status(status)
                      .entity(new ErrorResponse(message))
                      .type(MediaType.APPLICATION_JSON_TYPE)
                      .build());
    }
}
