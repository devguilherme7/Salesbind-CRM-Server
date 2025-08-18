package org.salesbind.exception;

import org.salesbind.infrastructure.exception.AbstractStatusResponseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends AbstractStatusResponseException {

    public EmailAlreadyRegisteredException() {
        super(HttpStatus.CONFLICT, "Email already exists");
    }
}
