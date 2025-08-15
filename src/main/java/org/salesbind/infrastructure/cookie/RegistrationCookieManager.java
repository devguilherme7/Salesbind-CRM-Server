package org.salesbind.infrastructure.cookie;

import org.salesbind.infrastructure.configuration.RegistrationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * This class encapsulates the logic for building the sign-up flow cookie,
 * deriving its configuration from {@link RegistrationProperties}.
 */
@Component
public class RegistrationCookieManager {

    public static final String SIGNUP_FLOW_ID_COOKIE_NAME = "SIGNUP_FLOW_ID";
    private static final String REGISTRATION_PATH = "/v1/registrations";

    private final RegistrationProperties registrationProperties;

    public RegistrationCookieManager(RegistrationProperties registrationProperties) {
        this.registrationProperties = registrationProperties;
    }

    public ResponseCookie createCookie(String value) {
        long maxAgeSeconds = registrationProperties.getTtl().toSeconds();
        return ResponseCookie.from(SIGNUP_FLOW_ID_COOKIE_NAME, value)
                .httpOnly(true)
                .maxAge(maxAgeSeconds)
                .path(REGISTRATION_PATH)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie clearCookie() {
        return ResponseCookie.from(SIGNUP_FLOW_ID_COOKIE_NAME, "")
                .maxAge(0)
                .path(REGISTRATION_PATH)
                .build();
    }
}
