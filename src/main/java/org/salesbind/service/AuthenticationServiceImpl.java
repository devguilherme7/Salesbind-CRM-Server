package org.salesbind.service;

import org.salesbind.dto.LoginRequest;
import org.salesbind.infrastructure.security.SecurityUser;
import org.salesbind.infrastructure.security.jwt.AccessTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AccessTokenProvider accessTokenProvider;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, AccessTokenProvider accessTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public String authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var principal = (SecurityUser) authentication.getPrincipal();
        return accessTokenProvider.generateToken(principal);
    }
}
