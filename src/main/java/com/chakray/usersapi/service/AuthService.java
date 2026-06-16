package com.chakray.usersapi.service;

import com.chakray.usersapi.dto.LoginRequest;
import com.chakray.usersapi.dto.LoginResponse;
import com.chakray.usersapi.exception.InvalidCredentialsException;
import com.chakray.usersapi.model.User;
import com.chakray.usersapi.repository.UserRepository;
import com.chakray.usersapi.security.Aes256Util;
import com.chakray.usersapi.security.JwtUtil;
import org.springframework.stereotype.Service;

/**
 * Authenticates a user by tax_id (username) + password and issues a JWT.
 */
@Service
public class AuthService {

    private final UserRepository repository;
    private final Aes256Util aes256Util;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository repository, Aes256Util aes256Util, JwtUtil jwtUtil) {
        this.repository = repository;
        this.aes256Util = aes256Util;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = repository.findByTaxId(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!aes256Util.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getTaxId(), user.getId().toString());
        return new LoginResponse(token, jwtUtil.getExpirationMs());
    }
}
