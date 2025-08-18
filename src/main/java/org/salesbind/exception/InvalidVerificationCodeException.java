package org.salesbind.exception;

import org.salesbind.infrastructure.exception.BadRequestException;

public class InvalidVerificationCodeException extends BadRequestException {

    public InvalidVerificationCodeException() {
        super("Invalid verification code");
    }
}
