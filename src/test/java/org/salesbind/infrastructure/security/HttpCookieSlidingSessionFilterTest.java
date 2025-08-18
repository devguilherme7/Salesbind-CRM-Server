package org.salesbind.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.security.jwt.AccessTokenProvider;
import org.salesbind.infrastructure.web.AuthenticationStateRepository;
import org.salesbind.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpCookieSlidingSessionFilterTest {

    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(10);
    private static final UUID TEST_USER_ID = UUID.randomUUID();

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private AuthenticationStateRepository authenticationStateRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private HttpCookieSlidingSessionFilter slidingSessionFilter;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = mock(AppUser.class);
        testUser.setId(TEST_USER_ID);
    }

    @Test
    void shouldRefreshTokenWhenThresholdReached() throws Exception {
        // Token is 60% old, past the 50% threshold
        Instant issuedAt = Instant.now().minus(TOKEN_LIFETIME.multipliedBy(6).dividedBy(10));
        Instant expiresAt = issuedAt.plus(TOKEN_LIFETIME);

        Jwt jwt = createJwt(issuedAt, expiresAt);
        setAuthentication(new JwtAuthenticationToken(jwt));

        when(appUserRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(accessTokenProvider.generateToken(any(SecurityUser.class))).thenReturn("new-refreshed-token");

        slidingSessionFilter.doFilterInternal(request, response, filterChain);

        verify(appUserRepository).findById(TEST_USER_ID);
        verify(accessTokenProvider).generateToken(any(SecurityUser.class));
        verify(authenticationStateRepository).saveAccessToken(eq("new-refreshed-token"), eq(response));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotRefreshTokenWhenBelowThreshold() throws Exception {
        Instant issuedAt = Instant.now().minus(TOKEN_LIFETIME.dividedBy(4));
        Instant expiresAt = issuedAt.plus(TOKEN_LIFETIME);
        Jwt jwt = createJwt(issuedAt, expiresAt);
        setAuthentication(new JwtAuthenticationToken(jwt));

        slidingSessionFilter.doFilterInternal(request, response, filterChain);

        verify(appUserRepository, never()).findById(any());
        verify(accessTokenProvider, never()).generateToken(any());
        verify(authenticationStateRepository, never()).saveAccessToken(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldDoNothingWhenSecurityContextIsEmpty() throws Exception {
        SecurityContextHolder.clearContext();

        slidingSessionFilter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(appUserRepository, accessTokenProvider, authenticationStateRepository);
        verify(filterChain).doFilter(request, response);
    }

    private Jwt createJwt(Instant issuedAt, Instant expiresAt) {
        return Jwt.withTokenValue("token-value")
                .subject(TEST_USER_ID.toString())
                .header("alg", "none")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}