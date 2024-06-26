package io.crossplane.compositefunctions.starter.exception;

/**
 * Exception for errors when unmarhsalling
 */
public class CrossplaneUnmarshallException extends RuntimeException {

    /**
     * Constructor with message
     * @param message The exception message
     */
    public CrossplaneUnmarshallException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * @param message The exception message
     * @param cause The throwable that caused the exception
     */
    public CrossplaneUnmarshallException(String message, Throwable cause) {
        super(message, cause);
    }
}
