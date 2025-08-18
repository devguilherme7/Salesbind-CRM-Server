package org.salesbind.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.salesbind.dto.LoginRequest;
import org.salesbind.infrastructure.web.AuthenticationStateRepository;
import org.salesbind.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/authentication")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationStateRepository authenticationStateRepository;

    public AuthenticationController(AuthenticationService authenticationService,
            AuthenticationStateRepository authenticationStateRepository) {
        this.authenticationService = authenticationService;
        this.authenticationStateRepository = authenticationStateRepository;
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a user with their email and password. Upon success, it returns an "
                    + "HTTP-only cookie named 'AUTH_TOKEN' containing the JWT access token.")
    @ApiResponse(responseCode = "200", description = "Authentication successful. Cookie is set.")
    @ApiResponse(responseCode = "400", description = "Invalid request body, e.g., malformed email.")
    @ApiResponse(responseCode = "401", description = "Invalid credentials provided.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {

        String accessToken = authenticationService.authenticate(request);
        authenticationStateRepository.saveAccessToken(accessToken, httpResponse);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        authenticationStateRepository.removeAccessToken(httpRequest, httpResponse);
        return ResponseEntity.noContent().build();
    }
}
