package org.salesbind.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record RequestEmailVerificationRequest(
        @NotBlank @Email
        @Schema(example = "yourname@organization.com")
        String email
) {

}
