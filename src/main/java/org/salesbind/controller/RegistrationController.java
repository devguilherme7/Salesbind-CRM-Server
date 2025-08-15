package org.salesbind.controller;

import jakarta.validation.Valid;
import org.salesbind.dto.RequestEmailVerificationRequest;
import org.salesbind.dto.VerifyCodeRequest;
import org.salesbind.infrastructure.configuration.RegistrationProperties;
import org.salesbind.service.RegistrationService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/registrations")
public class RegistrationController {

    private static final String SIGNUP_FLOW_ID_COOKIE_NAME = "SIGNUP_FLOW_ID";

    private final RegistrationService registrationService;
    private final RegistrationProperties registrationProperties;

    public RegistrationController(RegistrationService registrationService,
            RegistrationProperties registrationProperties) {
        this.registrationService = registrationService;
        this.registrationProperties = registrationProperties;
    }

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

    @PostMapping("/verify-code")
    public ResponseEntity<Void> verifyCode(@CookieValue(SIGNUP_FLOW_ID_COOKIE_NAME) String provisionId,
            @Valid @RequestBody VerifyCodeRequest request) {

        registrationService.verifyCode(provisionId, request.verificationCode());
        return ResponseEntity.noContent().build();
    }
}
