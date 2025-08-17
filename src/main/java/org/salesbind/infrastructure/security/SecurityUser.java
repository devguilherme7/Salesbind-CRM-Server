package org.salesbind.infrastructure.security;

import org.salesbind.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of {@link UserDetails} that wraps an {@link AppUser} entity.
 * <p>
 * This class acts as an adapter between the application's domain model (AppUser)
 * and Spring Security's core interfaces, decoupling the entity from the security framework.
 */
public record SecurityUser(AppUser appUser) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER")); // TODO
    }

    @Override
    public String getPassword() {
        return appUser.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return appUser.getEmail();
    }
}
