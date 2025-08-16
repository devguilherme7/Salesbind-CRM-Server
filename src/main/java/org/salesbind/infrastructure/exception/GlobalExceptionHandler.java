package org.salesbind.infrastructure.exception;

import org.salesbind.exception.CodeRequestTooSoonException;
import org.salesbind.exception.TooManyFailedAttemptsException;
import org.salesbind.infrastructure.cookie.RegistrationCookieManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final RegistrationCookieManager registrationCookieManager;

    public GlobalExceptionHandler(RegistrationCookieManager registrationCookieManager) {
        this.registrationCookieManager = registrationCookieManager;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        List<GlobalApiErrorResponse.FieldValidationError> validationErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> new GlobalApiErrorResponse.FieldValidationError(
                        err.getField(), err.getDefaultMessage()))
                .toList();

        var response = new GlobalApiErrorResponse("Validation failed for request", validationErrors);
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(TooManyFailedAttemptsException.class)
    public ResponseEntity<GlobalApiErrorResponse> handleTooManyFailedAttemptsException(
            TooManyFailedAttemptsException ex) {

        var response = new GlobalApiErrorResponse(ex.getMessage());
        ResponseCookie clearCookie = registrationCookieManager.clearCookie();

        return ResponseEntity.status(ex.getStatus())
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(response);
    }

    @ExceptionHandler(CodeRequestTooSoonException.class)
    public ResponseEntity<GlobalApiErrorResponse> handleCodeRequestTooSoonException(CodeRequestTooSoonException ex) {
        var response = new GlobalApiErrorResponse(ex.getMessage());
        String retryAfterSeconds = String.valueOf(ex.getRemainingCooldown().toSeconds());

        return ResponseEntity.status(ex.getStatus())
                .header(HttpHeaders.RETRY_AFTER, retryAfterSeconds)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        if (ex instanceof AbstractStatusResponseException exception) {
            var response = new GlobalApiErrorResponse(exception.getMessage());
            return ResponseEntity.status(exception.getStatus()).body(response);
        }

        LOG.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
