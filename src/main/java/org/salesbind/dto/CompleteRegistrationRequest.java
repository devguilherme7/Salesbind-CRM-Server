package org.salesbind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompleteRegistrationRequest(
        @NotBlank @Size(min = 3, max = 50)
        @Schema(example = "John")
        String firstName,

        @NotBlank @Size(min = 3, max = 50)
        @Schema(example = "Doe")
        String lastName,

        @NotBlank
        @Schema(example = "P@ssw0rd")
        String password,

        @NotBlank @Size(min = 1, max = 200)
        @Schema(example = "John's Company")
        String organizationName
) {

}