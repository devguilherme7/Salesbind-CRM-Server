package org.salesbind.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequest(@NotBlank String verificationCode) {

}
