package org.salesbind.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthenticationController {

    private static final String AUTH_TOKEN_COOKIE = "AUTH_TOKEN";
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a user with their email and password. Upon success, it returns an " +
                    "HTTP-only cookie named 'AUTH_TOKEN' containing the JWT access token.")
    @ApiResponse(responseCode = "200", description = "Authentication successful. Cookie is set.")
    @ApiResponse(responseCode = "400", description = "Invalid request body, e.g., malformed email.")
    @ApiResponse(responseCode = "401", description = "Invalid credentials provided.")
    @PostMapping("/login")
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
