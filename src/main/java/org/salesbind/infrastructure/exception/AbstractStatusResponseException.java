package org.salesbind.infrastructure.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractStatusResponseException extends RuntimeException {

    private final int status;

    protected AbstractStatusResponseException(HttpStatus status, String message) {
        super(message);
        this.status = status.value();
    }

    public int getStatus() {
        return status;
    }
}
