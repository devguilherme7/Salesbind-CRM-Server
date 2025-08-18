package org.salesbind.repository;

import org.salesbind.entity.RegistrationAttempt;
import java.time.Duration;
import java.util.Optional;

public interface RegistrationAttemptRepository {

    void save(RegistrationAttempt attempt, Duration ttl);

    Optional<RegistrationAttempt> findByProvisionId(String provisionId);

    Optional<RegistrationAttempt> findByEmail(String email);

    void delete(RegistrationAttempt attempt);
}
