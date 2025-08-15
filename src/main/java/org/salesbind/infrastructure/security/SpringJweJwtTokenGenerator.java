package org.salesbind.infrastructure.security;

import org.salesbind.entity.AppUser;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;

@Service
public class SpringJweJwtTokenGenerator implements JwtTokenGenerator {

    private final JwtEncoder jwtEncoder;

    public SpringJweJwtTokenGenerator(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateToken(AppUser appUser) {
        Instant now = Instant.now();

        //noinspection DataFlowIssue
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(appUser.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofDays(30)))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
