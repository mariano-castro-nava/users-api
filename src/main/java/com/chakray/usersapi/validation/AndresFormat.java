package com.chakray.usersapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * "AndresFormat" phone validation requested by the technical test.
 *
 * Rule (documented interpretation): after removing whitespace and an optional
 * leading country code (a '+' followed by 1 to 3 digits), the national number
 * must contain exactly 10 numeric digits and no other characters.
 *
 * Valid examples:  "+1 55 555 555 55", "5512345678", "+52 5512345678"
 * Invalid:         "12345", "55-555", "+1 555 555 5555 9" (11 national digits)
 *
 * A {@code null} value is considered valid so the same annotation can be reused
 * on PATCH (partial update) payloads; use it together with @NotBlank when the
 * field is mandatory.
 */
@Documented
@Constraint(validatedBy = AndresFormatValidator.class)
@Target({FIELD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface AndresFormat {

    String message() default "phone must pass the AndresFormat validation (10 national digits, optional country code)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
