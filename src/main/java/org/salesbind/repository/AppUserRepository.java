package org.salesbind.repository;

import java.util.Optional;
import java.util.UUID;

import org.salesbind.entity.AppUser;

public interface AppUserRepository {

    AppUser save(AppUser appUser);

    Optional<AppUser> findById(UUID id);

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
