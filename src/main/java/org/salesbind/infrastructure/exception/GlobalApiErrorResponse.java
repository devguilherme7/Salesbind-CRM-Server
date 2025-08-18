package org.salesbind.infrastructure.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GlobalApiErrorResponse(String message, List<FieldValidationError> errors) {

    public GlobalApiErrorResponse(String message) {
        this(message, null);
    }

    public record FieldValidationError(String field, String message) {

    }
}
