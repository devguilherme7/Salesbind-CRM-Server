package org.salesbind.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.salesbind.infrastructure.persistence.jpa.entity.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, UUID> {

}
