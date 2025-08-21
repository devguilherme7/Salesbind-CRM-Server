package org.salesbind.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.salesbind.infrastructure.persistence.jpa.entity.OrganizationMemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMemberJpaRepository extends JpaRepository<OrganizationMemberJpaEntity, UUID> {

}
