package org.salesbind.repository;

import org.salesbind.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {

}