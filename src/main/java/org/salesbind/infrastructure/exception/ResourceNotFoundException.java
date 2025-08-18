package org.salesbind.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AbstractStatusResponseException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
