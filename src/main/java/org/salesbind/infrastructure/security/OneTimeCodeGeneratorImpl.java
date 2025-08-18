package org.salesbind.infrastructure.security;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class OneTimeCodeGeneratorImpl implements OneTimeCodeGenerator {

    private static final String ALPHANUMERIC_CHARS = "0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARS.length());
            sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
