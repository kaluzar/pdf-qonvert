package com.pdf.convert.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model class for structured error responses.
 */
public class ErrorResponse {

    private final String timestamp;
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;

    /**
     * Creates a new ErrorResponse.
     *
     * @param status The HTTP status code
     * @param error The error type
     * @param code The application-specific error code
     * @param message The error message
     * @param path The request path
     */
    public ErrorResponse(int status, String error, String code, String message, String path) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
} 