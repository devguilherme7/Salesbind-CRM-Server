package org.salesbind.infrastructure.security;

public interface AppPasswordEncoder {

    String encode(String rawPassword);
}
