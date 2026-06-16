package com.chakray.usersapi.service;

import com.chakray.usersapi.dto.LoginRequest;
import com.chakray.usersapi.dto.LoginResponse;
import com.chakray.usersapi.exception.InvalidCredentialsException;
import com.chakray.usersapi.model.User;
import com.chakray.usersapi.repository.UserRepository;
import com.chakray.usersapi.security.Aes256Util;
import com.chakray.usersapi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private Aes256Util aes256Util;
    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(repository, aes256Util, jwtUtil);
    }

    private User demoUser() {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setTaxId("AARR990101XXX");
        u.setPassword("ENCRYPTED");
        return u;
    }

    @Test
    void loginWithValidCredentialsReturnsToken() {
        User user = demoUser();
        when(repository.findByTaxId("AARR990101XXX")).thenReturn(Optional.of(user));
        when(aes256Util.matches("password1", "ENCRYPTED")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(jwtUtil.getExpirationMs()).thenReturn(3600000L);

        LoginRequest req = new LoginRequest();
        req.setUsername("AARR990101XXX");
        req.setPassword("password1");

        LoginResponse response = authService.login(req);

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(3600000L, response.getExpiresIn());
    }

    @Test
    void loginWithUnknownUserThrows() {
        when(repository.findByTaxId("NOPE")).thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest();
        req.setUsername("NOPE");
        req.setPassword("x");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void loginWithWrongPasswordThrows() {
        User user = demoUser();
        when(repository.findByTaxId("AARR990101XXX")).thenReturn(Optional.of(user));
        when(aes256Util.matches("bad", "ENCRYPTED")).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setUsername("AARR990101XXX");
        req.setPassword("bad");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }
}
