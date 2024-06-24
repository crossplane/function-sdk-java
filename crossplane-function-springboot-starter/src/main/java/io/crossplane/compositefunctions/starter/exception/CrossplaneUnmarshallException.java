package io.crossplane.compositefunctions.starter.exception;

public class CrossplaneUnmarshallException extends RuntimeException {

    public CrossplaneUnmarshallException(String message) {
        super(message);
    }

    public CrossplaneUnmarshallException(String message, Throwable cause) {
        super(message, cause);
    }
}
