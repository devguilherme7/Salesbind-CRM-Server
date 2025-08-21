package org.salesbind.infrastructure.persistence.repository;

import org.salesbind.entity.OrganizationMember;
import org.salesbind.infrastructure.persistence.jpa.entity.OrganizationMemberJpaEntity;
import org.salesbind.infrastructure.persistence.jpa.mapper.OrganizationMemberJpaMapper;
import org.salesbind.infrastructure.persistence.jpa.repository.OrganizationMemberJpaRepository;
import org.salesbind.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationMemberRepositoryAdapter implements OrganizationMemberRepository {

    private final OrganizationMemberJpaRepository jpaRepository;
    private final OrganizationMemberJpaMapper jpaMapper;

    public OrganizationMemberRepositoryAdapter(OrganizationMemberJpaRepository jpaRepository,
            OrganizationMemberJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public OrganizationMember save(OrganizationMember organizationMember) {
        OrganizationMemberJpaEntity entity = jpaMapper.toEntity(organizationMember);
        return jpaMapper.toDomain(jpaRepository.save(entity));
    }
}
