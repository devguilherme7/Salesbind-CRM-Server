package org.salesbind.infrastructure.email;

public interface EmailService {

    void sendVerificationCode(String to, String code);
}
