package org.salesbind.infrastructure.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.Optional;

@Component
public final class CookieBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        try {
            String token = delegate.resolve(request);
            if (StringUtils.hasText(token)) {
                return token;
            }
        } catch (Exception ignored) {
            // Ignore
        }

        if (request.getCookies() == null) {
            return null;
        }

        Optional<Cookie> cookieOpt = Arrays.stream(request.getCookies())
                .filter(c -> "AUTH_TOKEN".equals(c.getName()))
                .findFirst();

        if (cookieOpt.isEmpty()) {
            return null;
        }

        String value = cookieOpt.get().getValue();
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }
}
