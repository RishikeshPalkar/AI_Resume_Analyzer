package com.example.resumeanalyzer.auth;

import com.example.resumeanalyzer.auth.dto.AuthResponse;
import com.example.resumeanalyzer.auth.dto.ChangePasswordRequest;
import com.example.resumeanalyzer.auth.dto.RegisterRequest;
import com.example.resumeanalyzer.auth.service.AuthService;
import com.example.resumeanalyzer.auth.service.RefreshTokenService;
import com.example.resumeanalyzer.common.exception.BadRequestException;
import com.example.resumeanalyzer.security.JwtTokenProvider;
import com.example.resumeanalyzer.user.entity.User;
import com.example.resumeanalyzer.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                tokenProvider,
                refreshTokenService
        );
    }

    @Test
    @DisplayName("Should successfully register a new user with hashed password")
    void testSuccessfulRegistration() {
        RegisterRequest req = new RegisterRequest("Alice Smith", "alice@example.com", "SecureP@ss123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecureP@ss123")).thenReturn("hashed_pass");

        User savedUser = new User("Alice Smith", "alice@example.com", "hashed_pass");
        savedUser.setId(10L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenProvider.generateAccessToken(any())).thenReturn("mock.jwt.token");
        when(tokenProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(refreshTokenService.createRefreshSession(savedUser)).thenReturn("mock_refresh_token");

        AuthService.AuthResult result = authService.register(req);

        assertNotNull(result);
        assertEquals("mock_refresh_token", result.refreshToken());
        AuthResponse resp = result.response();
        assertEquals("mock.jwt.token", resp.getAccessToken());
        assertEquals("alice@example.com", resp.getUser().getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when email already registered")
    void testDuplicateEmail() {
        RegisterRequest req = new RegisterRequest("Alice Smith", "alice@example.com", "SecureP@ss123");
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update password when current password matches")
    void testChangePasswordSuccess() {
        User user = new User("Alice", "alice@example.com", "old_hash");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old_password", "old_hash")).thenReturn(true);
        when(passwordEncoder.matches("new_password", "old_hash")).thenReturn(false);
        when(passwordEncoder.encode("new_password")).thenReturn("new_hash");

        ChangePasswordRequest request = new ChangePasswordRequest("old_password", "new_password");
        authService.changePassword(1L, request);

        assertEquals("new_hash", user.getPasswordHash());
        verify(refreshTokenService).revokeAllSessionsForUser(user);
    }

    @Test
    @DisplayName("Should reject password change when current password is wrong")
    void testChangePasswordWrongCurrent() {
        User user = new User("Alice", "alice@example.com", "old_hash");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "old_hash")).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest("wrong_password", "new_password");
        assertThrows(BadRequestException.class, () -> authService.changePassword(1L, request));
    }
}
