package org.salesbind.infrastructure.security.jwt;

import java.time.Instant;

import org.salesbind.infrastructure.configuration.AuthenticationProperties;
import org.salesbind.infrastructure.security.SecurityUser;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * An implementation of {@link AccessTokenProvider} that uses Spring Security's
 * {@link JwtEncoder} to create JWTs.
 */
@Service
public class SpringJwtAccessTokenProvider implements AccessTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final AuthenticationProperties authenticationProperties;

    public SpringJwtAccessTokenProvider(JwtEncoder jwtEncoder, AuthenticationProperties authenticationProperties) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationProperties = authenticationProperties;
    }

    @Override
    public String generateToken(SecurityUser user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(user.appUser().getId()))
                .issuedAt(now)
                .expiresAt(now.plus(authenticationProperties.getJwtExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
