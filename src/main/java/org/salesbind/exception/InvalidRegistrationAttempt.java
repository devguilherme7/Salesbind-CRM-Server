package org.salesbind.exception;

import org.salesbind.infrastructure.exception.UnauthorizedException;

public class InvalidRegistrationAttempt extends UnauthorizedException {

    public InvalidRegistrationAttempt() {
        super("Invalid registration attempt");
    }
}
