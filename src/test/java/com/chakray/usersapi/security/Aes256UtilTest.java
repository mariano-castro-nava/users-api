package com.chakray.usersapi.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Aes256UtilTest {

    private final Aes256Util aes = new Aes256Util("unit-test-secret-key");

    @Test
    void encryptThenDecryptReturnsOriginal() {
        String plain = "S3cr3t-Password!";
        String encrypted = aes.encrypt(plain);

        assertNotEquals(plain, encrypted, "stored value must not equal the plaintext");
        assertEquals(plain, aes.decrypt(encrypted));
    }

    @Test
    void sameInputProducesDifferentCiphertext() {
        // random IV per encryption -> non-deterministic output
        assertNotEquals(aes.encrypt("hello"), aes.encrypt("hello"));
    }

    @Test
    void matchesValidatesPasswordCorrectly() {
        String encrypted = aes.encrypt("password1");

        assertTrue(aes.matches("password1", encrypted));
        assertFalse(aes.matches("wrong", encrypted));
    }
}
