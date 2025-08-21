package org.salesbind.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.salesbind.infrastructure.persistence.jpa.entity.AppUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserJpaRepository extends JpaRepository<AppUserJpaEntity, UUID> {

    Optional<AppUserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
