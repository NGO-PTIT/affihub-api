package com.affihub.lib;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(ApiExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", exception.getMessage()))
                    .build();
        }

        if (exception instanceof WebApplicationException) {
            WebApplicationException webException = (WebApplicationException) exception;
            Response response = webException.getResponse();
            if (response.hasEntity()) {
                return response;
            }
            return Response.status(response.getStatus())
                    .entity(Map.of("error", webException.getMessage()))
                    .build();
        }

        LOGGER.log(Level.SEVERE, "Unhandled API exception", exception);
        return Response.serverError()
                .entity(Map.of("error", "Internal server error"))
                .build();
    }
}
