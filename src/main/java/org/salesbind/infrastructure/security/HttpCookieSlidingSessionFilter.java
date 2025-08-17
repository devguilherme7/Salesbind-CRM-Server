package org.salesbind.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.security.jwt.AccessTokenProvider;
import org.salesbind.infrastructure.web.AuthenticationStateRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;

/**
 * Implements a sliding session mechanism by refreshing the JWT if it's nearing expiration.
 */
@Component
public class HttpCookieSlidingSessionFilter extends OncePerRequestFilter {

    // Refresh if token is 50% or more through its validity period
    private static final double REFRESH_THRESHOLD = 0.5;

    private final AccessTokenProvider accessTokenProvider;
    private final AuthenticationStateRepository authenticationStateRepository;

    public HttpCookieSlidingSessionFilter(AccessTokenProvider accessTokenProvider,
            AuthenticationStateRepository authenticationStateRepository) {
        this.accessTokenProvider = accessTokenProvider;
        this.authenticationStateRepository = authenticationStateRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth
                && authentication.getPrincipal() instanceof AppUser user) {

            final Instant expiresAt = jwtAuth.getToken().getExpiresAt();
            final Instant issuedAt = jwtAuth.getToken().getIssuedAt();

            if (expiresAt != null && issuedAt != null) {
                final long lifetime = expiresAt.getEpochSecond() - issuedAt.getEpochSecond();
                final long timeElapsed = Instant.now().getEpochSecond() - issuedAt.getEpochSecond();

                if (timeElapsed > (lifetime * REFRESH_THRESHOLD)) {
                    final String newToken = accessTokenProvider.generateToken(user);
                    authenticationStateRepository.saveAccessToken(newToken, response);
                }
            }
        }
    }
}
