package org.salesbind.service;

import org.salesbind.dto.UserResponse;
import java.util.UUID;

public interface UserService {

    UserResponse findById(UUID id);
}
