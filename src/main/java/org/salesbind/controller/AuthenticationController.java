package org.salesbind.controller;

import jakarta.validation.Valid;
import org.salesbind.dto.LoginRequest;
import org.salesbind.service.AuthenticationService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/authentication")
public class AuthenticationController {

    private static final String AUTH_TOKEN_COOKIE = "AUTH_TOKEN";
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@Valid @RequestBody LoginRequest request) {
        String accessToken = authenticationService.authenticate(request);

        ResponseCookie authCookie = ResponseCookie.from(AUTH_TOKEN_COOKIE)
                .value(accessToken)
                .secure(true)
                .httpOnly(true)
                .maxAge(3600)
                .sameSite("Strict")
                .path("/")
                .build();

        return ResponseEntity.ok().header("Set-Cookie", authCookie.toString()).build();
    }
}
