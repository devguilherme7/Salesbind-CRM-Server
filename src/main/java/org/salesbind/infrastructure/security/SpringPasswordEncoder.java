package org.salesbind.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SpringPasswordEncoder implements AppPasswordEncoder {

    private final PasswordEncoder springPasswordEncoder;

    public SpringPasswordEncoder(PasswordEncoder springPasswordEncoder) {
        this.springPasswordEncoder = springPasswordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return springPasswordEncoder.encode(rawPassword);
    }
}
