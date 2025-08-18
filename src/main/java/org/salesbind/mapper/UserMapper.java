package org.salesbind.mapper;

import org.mapstruct.Mapper;
import org.salesbind.dto.UserResponse;
import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.configuration.GlobalMapperConfiguration;

@Mapper(config = GlobalMapperConfiguration.class)
public interface UserMapper {

    UserResponse toUserResponse(AppUser appUser);
}
