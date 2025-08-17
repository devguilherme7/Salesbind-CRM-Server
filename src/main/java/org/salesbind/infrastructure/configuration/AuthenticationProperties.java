package org.salesbind.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

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
