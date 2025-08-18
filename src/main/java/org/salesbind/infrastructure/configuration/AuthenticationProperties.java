package org.salesbind.infrastructure.configuration;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "salesbind.authentication")
public class AuthenticationProperties {

    private String secretKey;
    private Duration jwtExpiration = Duration.ofDays(30);

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Duration getJwtExpiration() {
        return jwtExpiration;
    }

    public void setJwtExpiration(Duration jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
    }
}
