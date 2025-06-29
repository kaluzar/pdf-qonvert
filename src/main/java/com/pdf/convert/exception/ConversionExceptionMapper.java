package com.pdf.convert.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * ExceptionMapper for ConversionException.
 * Converts ConversionException to HTTP responses with structured error information.
 */
@Provider
public class ConversionExceptionMapper implements ExceptionMapper<ConversionException> {

    private static final Logger LOG = Logger.getLogger(ConversionExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConversionException exception) {
        LOG.error("Conversion error: " + exception.getMessage(), exception);
        
        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Conversion Error",
                exception.getErrorCode(),
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "unknown"
        );
        
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
} 