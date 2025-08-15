package org.salesbind.exception;

import java.time.Duration;

public class CodeRequestTooSoonException extends RuntimeException {

    public CodeRequestTooSoonException(Duration remainingCooldown) {
        super(String.format("Must wait %d seconds before requesting a new code",
                remainingCooldown.getSeconds()));
    }
}
