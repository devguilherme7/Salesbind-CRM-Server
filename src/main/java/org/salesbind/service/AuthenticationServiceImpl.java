package org.salesbind.service;

import org.salesbind.dto.LoginRequest;
import org.salesbind.entity.AppUser;
import org.salesbind.infrastructure.security.JwtTokenGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtTokenGenerator jwtTokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public String authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var principal = (AppUser) authentication.getPrincipal();
        return jwtTokenGenerator.generateToken(principal);
    }
}
