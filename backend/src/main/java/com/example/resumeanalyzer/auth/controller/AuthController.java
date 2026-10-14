package com.example.resumeanalyzer.auth.controller;

import com.example.resumeanalyzer.auth.dto.*;
import com.example.resumeanalyzer.auth.service.AuthService;
import com.example.resumeanalyzer.common.response.ApiResponse;
import com.example.resumeanalyzer.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and session handling")
public class AuthController {

    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthService authService;
    private final boolean isProduction;

    public AuthController(
            AuthService authService,
            @Value("${spring.profiles.active:dev}") String activeProfile) {
        this.authService = authService;
        this.isProduction = "prod".equalsIgnoreCase(activeProfile);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthService.AuthResult result = authService.register(request);
        setRefreshTokenCookie(response, result.refreshToken(), 7 * 24 * 3600);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", result.response()));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthService.AuthResult result = authService.login(request);
        setRefreshTokenCookie(response, result.refreshToken(), 7 * 24 * 3600);
        return ResponseEntity.ok(ApiResponse.success("Login successful", result.response()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and issue new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        AuthService.AuthResult result = authService.refresh(refreshToken);
        setRefreshTokenCookie(response, result.refreshToken(), 7 * 24 * 3600);
        return ResponseEntity.ok(ApiResponse.success("Session refreshed", result.response()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke session and clear cookies")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.successMessage("Successfully logged out"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new com.example.resumeanalyzer.common.exception.UnauthorizedException("User is not authenticated");
        }
        UserProfileResponse profile = authService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change account password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletResponse response) {
        if (principal == null) {
            throw new com.example.resumeanalyzer.common.exception.UnauthorizedException("User is not authenticated");
        }
        authService.changePassword(principal.getId(), request);
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.successMessage("Password updated successfully. Please log in again."));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(isProduction)
                .path("/api/v1/auth")
                .maxAge(maxAgeSeconds)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(isProduction)
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
