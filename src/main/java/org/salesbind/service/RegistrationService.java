package org.salesbind.service;

import org.salesbind.dto.CompleteRegistrationRequest;

public interface RegistrationService {

    String requestEmailVerification(String email);

    void verifyCode(String provisionId, String verificationCode);

    void completeRegistration(String provisionId, CompleteRegistrationRequest request);
}
