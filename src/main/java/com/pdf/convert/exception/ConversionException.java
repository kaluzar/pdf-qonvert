package com.pdf.convert.exception;

/**
 * Exception thrown when PDF to DOCX conversion fails.
 */
public class ConversionException extends RuntimeException {

    private final String errorCode;
    
    /**
     * Creates a new ConversionException with a message and error code.
     *
     * @param message The error message
     * @param errorCode The error code
     */
    public ConversionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Creates a new ConversionException with a message, cause, and error code.
     *
     * @param message The error message
     * @param cause The cause of the exception
     * @param errorCode The error code
     */
    public ConversionException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Gets the error code associated with this exception.
     *
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }
} 