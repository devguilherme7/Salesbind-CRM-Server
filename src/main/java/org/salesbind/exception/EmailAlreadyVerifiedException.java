package org.salesbind.exception;

import org.salesbind.infrastructure.exception.BadRequestException;

public class EmailAlreadyVerifiedException extends BadRequestException {

    public EmailAlreadyVerifiedException() {
        super("Email already verified");
    }
}
