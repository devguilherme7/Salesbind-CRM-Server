package org.salesbind.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.salesbind.dto.CompleteRegistrationRequest;
import org.salesbind.dto.RequestEmailVerificationRequest;
import org.salesbind.dto.VerifyCodeRequest;
import org.salesbind.infrastructure.configuration.RegistrationProperties;
import org.salesbind.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/registrations")
@Tag(name = "Registration", description = "Endpoints for registering new users")
public class RegistrationController {

    private static final String SIGNUP_FLOW_ID_COOKIE_NAME = "SIGNUP_FLOW_ID";

    private final RegistrationService registrationService;
    private final RegistrationProperties registrationProperties;

    public RegistrationController(RegistrationService registrationService,
            RegistrationProperties registrationProperties) {
        this.registrationService = registrationService;
        this.registrationProperties = registrationProperties;
    }

    @Operation(
            summary = "Request a verification code",
            description = "Initiates the registration process by sending a verification code to user's email. " +
                    "This endpoint can also be used to resend the code if the previous one was not received or " +
                    "has expired")
    @ApiResponse(responseCode = "202", description = "Verification code request accepted. An email has been dispatched")
    @ApiResponse(responseCode = "400", description = "The email may be invalid or this email already verified")
    @PostMapping("/request-verification-code")
    public ResponseEntity<Void> requestVerification(@Valid @RequestBody RequestEmailVerificationRequest request) {
        String sid = registrationService.requestEmailVerification(request.email());

        var maxAge = (int) registrationProperties.getTtl().toSeconds();
        ResponseCookie cookie = ResponseCookie.from(SIGNUP_FLOW_ID_COOKIE_NAME)
                .value(sid)
                .httpOnly(true)
                .maxAge(maxAge)
                .path("/v1/registrations")
                .secure(true)
                .sameSite("Strict")
                .build();

        return ResponseEntity.accepted().header("Set-Cookie", cookie.toString()).build();
    }

    @Operation(summary = "Verify the one-time code", description = "Verifies the code sent to the user's email")
    @ApiResponse(responseCode = "204", description = "Verification successful")
    @ApiResponse(responseCode = "400", description = "Invalid code or malformed request. " +
            "The '" + SIGNUP_FLOW_ID_COOKIE_NAME + "' cookie might be missing")
    @ApiResponse(responseCode = "401", description = "The verification session was not found or has expired")
    @ApiResponse(responseCode = "429", description = "Too many failed verification attempts")
    @PostMapping("/verify-code")
    public ResponseEntity<Void> verifyCode(@CookieValue(SIGNUP_FLOW_ID_COOKIE_NAME) String provisionId,
            @Valid @RequestBody VerifyCodeRequest request) {

        registrationService.verifyCode(provisionId, request.verificationCode());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Complete the registration process", description = "Finalizes the account creation after " +
            "successful email verification. This creates the user and their organization")
    @ApiResponse(responseCode = "201", description = "User and organization created successfully")
    @ApiResponse(responseCode = "400", description = "Input data is invalid (e.g., weak password) or the email has " +
            "not been verified yet")
    @ApiResponse(responseCode = "401", description = "The verification session was not found or has expired")
    @ApiResponse(responseCode = "409", description = "The provided email is already registered")
    @PostMapping("/complete")
    public ResponseEntity<Void> complete(@CookieValue(SIGNUP_FLOW_ID_COOKIE_NAME) String provisionId,
            @Valid @RequestBody CompleteRegistrationRequest request) {

        registrationService.completeRegistration(provisionId, request);
        ResponseCookie clearCookie = ResponseCookie.from(SIGNUP_FLOW_ID_COOKIE_NAME)
                .value("")
                .maxAge(0)
                .path("/v1/registrations")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).header("Set-Cookie", clearCookie.toString()).build();
    }
}
