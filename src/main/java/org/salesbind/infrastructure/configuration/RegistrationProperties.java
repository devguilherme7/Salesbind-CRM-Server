package org.salesbind.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "salesbind.registration")
public class RegistrationProperties {

    private Duration ttl;
    private OneTimeCodeProperties oneTimeCode;

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    public OneTimeCodeProperties getOneTimeCode() {
        return oneTimeCode;
    }

    public void setOneTimeCode(OneTimeCodeProperties oneTimeCode) {
        this.oneTimeCode = oneTimeCode;
    }

    public static class OneTimeCodeProperties {

        private int maxAttempts;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }
    }
}
