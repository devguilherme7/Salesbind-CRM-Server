package org.salesbind.exception;

public class TooManyFailedAttemptsException extends RuntimeException {

    public TooManyFailedAttemptsException() {
        super("Too many failed attempts");
    }
}
