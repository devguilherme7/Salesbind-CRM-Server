package org.salesbind.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.salesbind.entity.Organization;
import org.salesbind.infrastructure.configuration.GlobalMapperConfiguration;
import org.salesbind.infrastructure.persistence.jpa.entity.OrganizationJpaEntity;

@Mapper(config = GlobalMapperConfiguration.class)
public interface OrganizationJpaMapper {

    Organization toDomain(OrganizationJpaEntity entity);

    OrganizationJpaEntity toEntity(Organization domain);
}
