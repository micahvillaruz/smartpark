package com.smartpark.service;

import com.smartpark.dto.LoginRequest;
import com.smartpark.dto.LoginResponse;
import com.smartpark.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final String validUsername;
    private final String validPassword;

    public AuthService(JwtService jwtService,
                       @Value("${app.auth.username}") String validUsername,
                       @Value("${app.auth.password}") String validPassword) {
        this.jwtService = jwtService;
        this.validUsername = validUsername;
        this.validPassword = validPassword;
    }

    public LoginResponse login(LoginRequest request) {
        if (!validUsername.equals(request.getUsername()) || !validPassword.equals(request.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(request.getUsername());
        return new LoginResponse(token, jwtService.getExpirationMs());
    }
}