package org.salesbind.exception;

public class RegistrationAttemptNotFoundException extends RuntimeException {

    public RegistrationAttemptNotFoundException() {
        super("Invalid registration attempt");
    }
}
