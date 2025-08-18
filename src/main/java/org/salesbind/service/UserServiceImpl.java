package org.salesbind.service;

import java.util.UUID;

import org.salesbind.dto.UserResponse;
import org.salesbind.exception.UserNotFoundException;
import org.salesbind.mapper.UserMapper;
import org.salesbind.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(AppUserRepository appUserRepository, UserMapper userMapper) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return appUserRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(UserNotFoundException::new);
    }
}
