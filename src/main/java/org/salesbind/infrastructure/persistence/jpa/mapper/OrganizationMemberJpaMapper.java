package org.salesbind.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.salesbind.entity.OrganizationMember;
import org.salesbind.infrastructure.configuration.GlobalMapperConfiguration;
import org.salesbind.infrastructure.persistence.jpa.entity.OrganizationMemberJpaEntity;

@Mapper(config = GlobalMapperConfiguration.class, uses = {OrganizationJpaMapper.class})
public interface OrganizationMemberJpaMapper {

    OrganizationMember toDomain(OrganizationMemberJpaEntity entity);

    OrganizationMemberJpaEntity toEntity(OrganizationMember domain);
}
