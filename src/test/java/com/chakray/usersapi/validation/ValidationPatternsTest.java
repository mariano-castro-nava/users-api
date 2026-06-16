package com.chakray.usersapi.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationPatternsTest {

    @Test
    void acceptsValidRfc() {
        assertTrue("AARR990101XXX".matches(ValidationPatterns.RFC)); // 4 letters (fisica)
        assertTrue("BBSS880202YYY".matches(ValidationPatterns.RFC));
        assertTrue("ABC990101XY1".matches(ValidationPatterns.RFC));  // 3 letters (moral)
    }

    @Test
    void rejectsInvalidRfc() {
        assertFalse("aarr990101xxx".matches(ValidationPatterns.RFC)); // lower case
        assertFalse("AARR9901XXX".matches(ValidationPatterns.RFC));   // missing date digits
        assertFalse("12RR990101XXX".matches(ValidationPatterns.RFC)); // leading digits
        assertFalse("".matches(ValidationPatterns.RFC));
    }
}
