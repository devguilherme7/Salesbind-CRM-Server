package org.salesbind.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;

import org.salesbind.infrastructure.web.AuthenticationStateRepository;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public final class CookieBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
    private final AuthenticationStateRepository authenticationStateRepository;

    public CookieBearerTokenResolver(AuthenticationStateRepository authenticationStateRepository) {
        this.authenticationStateRepository = authenticationStateRepository;
    }

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

        return authenticationStateRepository.loadAccessToken(request).map(String::trim).orElse(null);
    }
}
