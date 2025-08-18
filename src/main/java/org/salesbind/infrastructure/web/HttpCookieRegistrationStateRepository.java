package org.salesbind.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.salesbind.infrastructure.configuration.RegistrationProperties;
import org.salesbind.infrastructure.util.CookieUtils;
import org.springframework.stereotype.Repository;

/**
 * An implementation of {@link RegistrationStateRepository} that stores the registration
 * flow state in an HTTP cookie.
 */
@Repository
public class HttpCookieRegistrationStateRepository implements RegistrationStateRepository {

    public static final String REGISTRATION_STATE_COOKIE = "REGISTRATION_STATE";
    public static final String REGISTRATION_PATH = "/v1/registrations";

    private final RegistrationProperties registrationProperties;

    public HttpCookieRegistrationStateRepository(RegistrationProperties registrationProperties) {
        this.registrationProperties = registrationProperties;
    }

    @Override
    public void saveRegistrationAttemptSessionId(String sessionId, HttpServletResponse response) {
        long expirationSeconds = registrationProperties.getTtl().toSeconds();
        CookieUtils.addCookie(response, REGISTRATION_STATE_COOKIE, sessionId, expirationSeconds, REGISTRATION_PATH);
    }

    @Override
    public void removeRegistrationState(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.removeCookie(request, response, REGISTRATION_STATE_COOKIE, REGISTRATION_PATH);
    }
}
