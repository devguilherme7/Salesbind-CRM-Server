package org.salesbind.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractStatusResponseException {

    protected UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
