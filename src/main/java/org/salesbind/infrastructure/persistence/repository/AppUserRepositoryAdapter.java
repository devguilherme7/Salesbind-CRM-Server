package org.salesbind.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.persistence.jpa.entity.AppUserJpaEntity;
import org.salesbind.infrastructure.persistence.jpa.mapper.AppUserJpaMapper;
import org.salesbind.infrastructure.persistence.jpa.repository.AppUserJpaRepository;
import org.salesbind.repository.AppUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AppUserRepositoryAdapter implements AppUserRepository {

    private final AppUserJpaRepository jpaRepository;
    private final AppUserJpaMapper jpaMapper;

    public AppUserRepositoryAdapter(AppUserJpaRepository jpaRepository, AppUserJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public AppUser save(AppUser appUser) {
        AppUserJpaEntity entity = jpaMapper.toEntity(appUser);
        return jpaMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<AppUser> findById(UUID id) {
        return jpaRepository.findById(id).map(jpaMapper::toDomain);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(jpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
