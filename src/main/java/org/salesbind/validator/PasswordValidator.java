package org.salesbind.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()\\-+=_].*");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(".*\\s.*");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            // Let @NotBlank handle this case
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            context.buildConstraintViolationWithTemplate(
                    String.format("must be between %d and %d characters", MIN_LENGTH, MAX_LENGTH)
            ).addConstraintViolation();
            return false;
        }

        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate("must contain at least one uppercase letter")
                    .addConstraintViolation();
            return false;
        }

        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate("must contain at least one lowercase letter")
                    .addConstraintViolation();
            return false;
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate("must contain at least one digit")
                    .addConstraintViolation();
            return false;
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate("must contain at least one special character")
                    .addConstraintViolation();
            return false;
        }

        if (WHITESPACE_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate("must not contain whitespace")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
