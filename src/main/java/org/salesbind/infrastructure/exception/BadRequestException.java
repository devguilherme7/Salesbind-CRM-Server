package org.salesbind.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractStatusResponseException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
