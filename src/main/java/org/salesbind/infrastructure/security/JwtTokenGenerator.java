package org.salesbind.infrastructure.security;

import org.salesbind.entity.AppUser;

public interface JwtTokenGenerator {

    String generateToken(AppUser appUser);
}
