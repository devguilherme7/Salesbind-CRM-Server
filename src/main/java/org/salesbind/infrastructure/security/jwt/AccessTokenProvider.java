package org.salesbind.infrastructure.security.jwt;

import org.salesbind.entity.AppUser;

/**
 * Defines a contract for providing access token for a given user.
 * <p>
 * This interface abstracts the underlying token generation mechanism (e.g., JWT, JWE)
 * and focuses on the business purpose of creating a session token.
 * </p>
 */
public interface AccessTokenProvider {

    /**
     * Generates a new access token for the specified user.
     *
     * @param appUser The user for whom to generate the token.
     * @return A string representation of the access token.
     */
    String generateToken(AppUser appUser);
}
