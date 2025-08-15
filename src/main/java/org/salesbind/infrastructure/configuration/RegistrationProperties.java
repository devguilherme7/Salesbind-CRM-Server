package org.salesbind.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "salesbind.registration")
public class RegistrationProperties {

    private Duration ttl;

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }
}
