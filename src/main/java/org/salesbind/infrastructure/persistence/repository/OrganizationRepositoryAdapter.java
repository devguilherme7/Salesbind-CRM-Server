package org.salesbind.infrastructure.persistence.repository;

import org.salesbind.entity.Organization;
import org.salesbind.infrastructure.persistence.jpa.entity.OrganizationJpaEntity;
import org.salesbind.infrastructure.persistence.jpa.mapper.OrganizationJpaMapper;
import org.salesbind.infrastructure.persistence.jpa.repository.OrganizationJpaRepository;
import org.salesbind.repository.OrganizationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final OrganizationJpaRepository jpaRepository;
    private final OrganizationJpaMapper jpaMapper;

    public OrganizationRepositoryAdapter(OrganizationJpaRepository jpaRepository, OrganizationJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public Organization save(Organization organization) {
        OrganizationJpaEntity entity = jpaMapper.toEntity(organization);
        return jpaMapper.toDomain(jpaRepository.save(entity));
    }
}
