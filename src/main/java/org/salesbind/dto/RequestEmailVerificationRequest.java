package org.salesbind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestEmailVerificationRequest(
        @NotBlank @Email
        @Schema(example = "yourname@organization.com")
        String email
) {

}
