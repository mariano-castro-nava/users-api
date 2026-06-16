package com.chakray.usersapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Implementation of the {@link AndresFormat} constraint.
 */
public class AndresFormatValidator implements ConstraintValidator<AndresFormat, String> {

    /** Optional country code: a '+' followed by 1-3 digits at the start of the string. */
    private static final Pattern COUNTRY_CODE = Pattern.compile("^\\+\\d{1,3}");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null is delegated to @NotBlank when the field is mandatory
        if (value == null) {
            return true;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // Remove an optional leading country code (+XX) and then all whitespace.
        String national = COUNTRY_CODE.matcher(trimmed).replaceFirst("");
        national = national.replaceAll("\\s+", "");

        // What remains must be exactly 10 numeric digits.
        return national.matches("\\d{10}");
    }
}
