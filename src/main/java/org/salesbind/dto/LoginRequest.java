package org.salesbind.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @NotBlank @Email
        @Schema(example = "john.doe@example.com")
        String email,

        @NotBlank
        @Schema(example = "P@ssw0rd!")
        String password) {

}
