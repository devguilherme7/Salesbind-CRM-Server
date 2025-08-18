package org.salesbind.service;

import java.util.UUID;

import org.salesbind.dto.UserResponse;

public interface UserService {

    UserResponse findById(UUID id);
}
