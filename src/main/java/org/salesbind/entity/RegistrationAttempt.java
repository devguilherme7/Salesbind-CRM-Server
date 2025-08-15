package org.salesbind.entity;

import java.time.Duration;
import java.time.Instant;

@SuppressWarnings("unused")
public class RegistrationAttempt {

    private String provisionId;
    private String email;
    private VerificationCode verificationCode;
    private Instant verificationCodeExpiresAt;
    private boolean verified;
    private int failedAttempts;
    private Instant lastCodeRequestAt;

    public RegistrationAttempt(String provisionId, String email) {
        this.provisionId = provisionId;
        this.email = email;
        this.verified = false;
    }

    protected RegistrationAttempt() {
    }

    public void assignVerificationCode(VerificationCode verificationCode, Instant expiresAt) {
        this.verificationCode = verificationCode;
        this.verificationCodeExpiresAt = expiresAt;
        this.verified = false;
        this.failedAttempts = 0;
        this.lastCodeRequestAt = Instant.now();
    }

    public boolean hasExceededMaxAttempts(int maxAttempts) {
        return this.failedAttempts >= maxAttempts;
    }

    public boolean verifyCode(VerificationCode providedCode) {
        if (isExpired()) {
            return false;
        }

        if (this.verificationCode.equals(providedCode)) {
            this.verified = true;
            this.verificationCode = null;
            this.verificationCodeExpiresAt = null;
            return true;
        }

        this.failedAttempts++;
        return false;
    }

    public boolean canRequestNewCode(Duration cooldownPeriod) {
        if (lastCodeRequestAt == null) {
            // No code has been sent yet, so no cooldown is active.
            return true;
        }

        return Instant.now().isAfter(lastCodeRequestAt.plus(cooldownPeriod));
    }

    public Duration getRemainingCooldown(Duration cooldownPeriod) {
        if (lastCodeRequestAt == null) {
            return Duration.ZERO;
        }

        Instant currentTime = Instant.now();
        Instant cooldownEndsAt = lastCodeRequestAt.plus(cooldownPeriod);
        if (currentTime.isAfter(cooldownEndsAt)) {
            return Duration.ZERO;
        }

        return Duration.between(currentTime, cooldownEndsAt);
    }

    private boolean isExpired() {
        return Instant.now().isAfter(verificationCodeExpiresAt);
    }

    public String getProvisionId() {
        return provisionId;
    }

    public String getEmail() {
        return email;
    }

    public VerificationCode getVerificationCode() {
        return verificationCode;
    }

    public Instant getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public Instant getLastCodeRequestAt() {
        return lastCodeRequestAt;
    }
}
