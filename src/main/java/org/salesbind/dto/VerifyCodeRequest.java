package org.salesbind.dto;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record VerifyCodeRequest(@NotBlank @Schema(example = "789321") String verificationCode) {

}
