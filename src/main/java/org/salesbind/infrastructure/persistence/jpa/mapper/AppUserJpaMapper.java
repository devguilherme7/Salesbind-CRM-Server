package org.salesbind.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.configuration.GlobalMapperConfiguration;
import org.salesbind.infrastructure.persistence.jpa.entity.AppUserJpaEntity;

@Mapper(config = GlobalMapperConfiguration.class)
public interface AppUserJpaMapper {

    AppUser toDomain(AppUserJpaEntity entity);

    AppUserJpaEntity toEntity(AppUser domain);
}
