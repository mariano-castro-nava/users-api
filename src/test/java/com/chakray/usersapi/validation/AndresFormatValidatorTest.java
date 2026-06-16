package com.chakray.usersapi.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AndresFormatValidatorTest {

    private final AndresFormatValidator validator = new AndresFormatValidator();

    private boolean valid(String value) {
        return validator.isValid(value, null);
    }

    @Test
    void acceptsTenDigitNumbersWithOptionalCountryCode() {
        assertTrue(valid("+1 55 555 555 55"));   // example from the spec
        assertTrue(valid("5512345678"));
        assertTrue(valid("+52 5512345678"));
        assertTrue(valid("+521 5512345678"));
    }

    @Test
    void nullIsConsideredValidForPartialUpdates() {
        assertTrue(valid(null));
    }

    @Test
    void rejectsWrongLengthOrInvalidCharacters() {
        assertFalse(valid(""));
        assertFalse(valid("12345"));            // too short
        assertFalse(valid("55123456789"));      // 11 digits
        assertFalse(valid("55-555-5555"));      // illegal separators
        assertFalse(valid("abcdefghij"));       // not numeric
    }
}
