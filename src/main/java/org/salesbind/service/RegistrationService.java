package org.salesbind.service;

public interface RegistrationService {

    String requestEmailVerification(String email);

    void verifyCode(String provisionId, String verificationCode);
}
