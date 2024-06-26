package io.crossplane.compositefunctions.starter.exception;

/**
 * Exception for unexpected items encountered when converting
 */
public class CrossplaneUnexpectedItemsException extends RuntimeException {

    /**
     * Constructor with message
     * @param message The exception message
     */
    public CrossplaneUnexpectedItemsException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * @param message The exception message
     * @param cause The throwable that caused the exception
     */
    public CrossplaneUnexpectedItemsException(String message, Throwable cause) {
        super(message, cause);
    }
}
