package com.chakray.usersapi.validation;

/**
 * Shared validation regular expressions.
 */
public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    /**
     * Mexican RFC (Registro Federal de Contribuyentes).
     * 3 letters (moral) or 4 letters (fisica) + 6 date digits (YYMMDD) + 3 char homoclave.
     * Example: AARR990101XXX
     * Ñ is the letter N-tilde, valid in the RFC alphabet.
     */
    public static final String RFC = "^[A-Z&Ñ]{3,4}\\d{6}[A-Z\\d]{3}$";
}
