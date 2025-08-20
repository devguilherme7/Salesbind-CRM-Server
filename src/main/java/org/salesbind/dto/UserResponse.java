package org.salesbind.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        UUID id,
        @Schema(example = "john.doe@example.com")
        String email,
        @Schema(example = "John")
        String firstName,
        @Schema(example = "Doe")
        String lastName) {

}
