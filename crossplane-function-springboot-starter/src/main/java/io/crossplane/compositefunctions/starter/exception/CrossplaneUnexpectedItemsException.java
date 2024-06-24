package io.crossplane.compositefunctions.starter.exception;

public class CrossplaneUnexpectedItemsException extends RuntimeException {

    public CrossplaneUnexpectedItemsException(String message) {
        super(message);
    }

    public CrossplaneUnexpectedItemsException(String message, Throwable cause) {
        super(message, cause);
    }
}
