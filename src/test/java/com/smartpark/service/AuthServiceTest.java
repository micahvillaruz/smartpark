package com.smartpark.service;

import com.smartpark.dto.LoginRequest;
import com.smartpark.dto.LoginResponse;
import com.smartpark.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        when(jwtService.generateToken(anyString())).thenReturn("dummy-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        authService = new AuthService(jwtService, "admin", "admin123");
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("admin", "admin123");

        LoginResponse response = authService.login(request);

        assertEquals("dummy-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(3600000L, response.getExpiresInMs());
    }

    @Test
    void login_throws_whenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("admin", "wrong");

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_throws_whenUsernameIsWrong() {
        LoginRequest request = new LoginRequest("hacker", "admin123");

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}