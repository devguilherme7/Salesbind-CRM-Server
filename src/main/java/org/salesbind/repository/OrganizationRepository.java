package org.salesbind.repository;

import java.util.UUID;

import org.salesbind.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

}
