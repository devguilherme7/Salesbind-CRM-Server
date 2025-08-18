package org.salesbind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.salesbind.validator.Password;

import io.swagger.v3.oas.annotations.media.Schema;

public record CompleteRegistrationRequest(
        @NotBlank @Size(min = 3, max = 50)
        @Schema(example = "John")
        String firstName,

        @NotBlank @Size(min = 3, max = 50)
        @Schema(example = "Doe")
        String lastName,

        @NotBlank
        @Password
        @Schema(example = "P@ssw0rd")
        String password,

        @NotBlank @Size(min = 1, max = 200)
        @Schema(example = "John's Company")
        String organizationName
) {

}
