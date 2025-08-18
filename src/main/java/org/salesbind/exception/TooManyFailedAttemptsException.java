package org.salesbind.exception;

import org.salesbind.infrastructure.exception.AbstractStatusResponseException;
import org.springframework.http.HttpStatus;

public class TooManyFailedAttemptsException extends AbstractStatusResponseException {

    public TooManyFailedAttemptsException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "Too many failed attempts");
    }
}
