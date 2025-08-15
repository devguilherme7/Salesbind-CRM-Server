package org.salesbind.entity;

import java.time.Instant;

public class RegistrationAttempt {

    private String provisionId;
    private String email;

    private VerificationCode verificationCode;
    private Instant verificationCodeExpiresAt;

    public RegistrationAttempt(String provisionId, String email) {
        this.provisionId = provisionId;
        this.email = email;
    }

    protected RegistrationAttempt() {
    }

    public void assignVerificationCode(VerificationCode verificationCode, Instant expiresAt) {
        this.verificationCode = verificationCode;
        this.verificationCodeExpiresAt = expiresAt;
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
}
