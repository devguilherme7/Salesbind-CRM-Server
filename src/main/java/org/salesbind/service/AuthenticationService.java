package org.salesbind.service;

import org.salesbind.dto.LoginRequest;

public interface AuthenticationService {

    String authenticate(LoginRequest request);
}
