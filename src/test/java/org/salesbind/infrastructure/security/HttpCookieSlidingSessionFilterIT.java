package org.salesbind.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.web.HttpCookieAuthenticationStateRepository;
import org.salesbind.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HttpCookieSlidingSessionFilterIT {

    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(15);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @TestConfiguration
    static class TestControllerConfiguration {

        @RestController
        static class TestController {

            @GetMapping("/api/test/secure")
            public ResponseEntity<String> secureEndpoint() {
                return ResponseEntity.ok("OK");
            }
        }
    }

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();

        testUser = AppUser.create("testuser@example.com", "Test", "User", "password");
        testUser.setId(id);
    }

    @Test
    void shouldReturnRefreshedTokenInCookieWhenRequestHasOldToken() throws Exception {
        Instant issuedAt = Instant.now().minus(TOKEN_LIFETIME.multipliedBy(6).dividedBy(10));
        Instant expiresAt = issuedAt.plus(TOKEN_LIFETIME);
        String oldToken = generateTestToken(issuedAt, expiresAt);
        Cookie authCookie = new Cookie(HttpCookieAuthenticationStateRepository.AUTH_TOKEN_COOKIE_NAME, oldToken);

        when(appUserRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        MvcResult result = mockMvc.perform(get("/api/test/secure").cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andReturn();

        String setCookieHeader = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeader)
                .contains(HttpCookieAuthenticationStateRepository.AUTH_TOKEN_COOKIE_NAME + "=")
                .doesNotContain(oldToken);
    }

    @Test
    void shouldDoNothingWhenRequestIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/test/secure"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
    }

    @SuppressWarnings("DataFlowIssue")
    private String generateTestToken(Instant issuedAt, Instant expiresAt) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(testUser.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
