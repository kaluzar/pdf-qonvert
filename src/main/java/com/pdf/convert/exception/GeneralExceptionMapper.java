package com.pdf.convert.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * ExceptionMapper for general exceptions.
 * Provides a fallback for exceptions not handled by specific mappers.
 */
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GeneralExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Unexpected error: " + exception.getMessage(), exception);
        
        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Internal Server Error",
                "ERR-INTERNAL",
                "An unexpected error occurred while processing the request",
                uriInfo != null ? uriInfo.getPath() : "unknown"
        );
        
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
} 