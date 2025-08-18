package org.salesbind.exception;

import org.salesbind.infrastructure.exception.AbstractStatusResponseException;
import org.springframework.http.HttpStatus;
import java.time.Duration;

public class CodeRequestTooSoonException extends AbstractStatusResponseException {

    private final Duration remainingCooldown;

    public CodeRequestTooSoonException(Duration remainingCooldown) {
        super(HttpStatus.TOO_MANY_REQUESTS, String.format("Must wait %d seconds before requesting a new code",
                remainingCooldown.getSeconds()));
        this.remainingCooldown = remainingCooldown;
    }

    public Duration getRemainingCooldown() {
        return remainingCooldown;
    }
}
