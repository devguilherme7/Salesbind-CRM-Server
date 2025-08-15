package org.salesbind.exception;

import org.salesbind.infrastructure.exception.AbstractStatusResponseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends AbstractStatusResponseException {

    public EmailAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Email already exists");
    }
}
