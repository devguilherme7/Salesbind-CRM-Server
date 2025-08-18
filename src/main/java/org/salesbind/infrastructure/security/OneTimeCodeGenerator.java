package org.salesbind.infrastructure.security;

public interface OneTimeCodeGenerator {

    String generate(int length);
}
