package com.lovedbug.geulgwi.core.common.exception;

public class GeulgwiException extends RuntimeException {

    public GeulgwiException() {
    }

    public GeulgwiException(String message) {
        super(message);
    }

    public GeulgwiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeulgwiException(Throwable cause) {
        super(cause);
    }
}
