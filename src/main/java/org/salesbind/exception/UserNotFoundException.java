package org.salesbind.exception;

import org.salesbind.infrastructure.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException() {
        super("User not found");
    }
}
