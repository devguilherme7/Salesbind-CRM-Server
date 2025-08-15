package org.salesbind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(@NotBlank @Schema(example = "789321") String verificationCode) {

}
