package org.salesbind.exception;

import org.salesbind.infrastructure.exception.BadRequestException;

public class EmailNotVerifiedException extends BadRequestException {

    public EmailNotVerifiedException() {
        super("Email not verified");
    }
}
