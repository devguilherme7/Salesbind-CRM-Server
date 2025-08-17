package org.salesbind.infrastructure.security.jwt;

import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.configuration.AuthenticationProperties;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;

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
    public String generateToken(AppUser appUser) {
        Instant now = Instant.now();

        //noinspection DataFlowIssue
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(appUser.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(authenticationProperties.getJwtExpiration()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
