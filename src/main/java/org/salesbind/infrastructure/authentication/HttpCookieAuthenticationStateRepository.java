package org.salesbind.infrastructure.authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.salesbind.infrastructure.configuration.AuthenticationProperties;
import org.salesbind.infrastructure.util.CookieUtils;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class HttpCookieAuthenticationStateRepository implements AuthenticationStateRepository {

    public static final String AUTH_TOKEN_COOKIE_NAME = "AUTH_TOKEN";

    private final AuthenticationProperties authenticationProperties;

    public HttpCookieAuthenticationStateRepository(AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    @Override
    public Optional<String> loadAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return CookieUtils.getCookie(request, AUTH_TOKEN_COOKIE_NAME).map(Cookie::getValue);
    }

    @Override
    public void saveAccessToken(String accessToken, HttpServletResponse response) {
        long expirationSeconds = authenticationProperties.getJwtExpiration().toSeconds();
        CookieUtils.addCookie(response, AUTH_TOKEN_COOKIE_NAME, accessToken, expirationSeconds);
    }

    @Override
    public void removeAccessToken(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.removeCookie(request, response, AUTH_TOKEN_COOKIE_NAME);
    }
}
