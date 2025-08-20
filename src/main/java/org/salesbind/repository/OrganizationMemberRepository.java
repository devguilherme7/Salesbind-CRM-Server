package org.salesbind.repository;

import java.util.UUID;

import org.salesbind.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {

}
