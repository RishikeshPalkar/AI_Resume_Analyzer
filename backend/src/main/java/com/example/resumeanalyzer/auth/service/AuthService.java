package com.example.resumeanalyzer.auth.service;

import com.example.resumeanalyzer.auth.dto.*;
import com.example.resumeanalyzer.common.exception.BadRequestException;
import com.example.resumeanalyzer.common.exception.ResourceNotFoundException;
import com.example.resumeanalyzer.common.exception.UnauthorizedException;
import com.example.resumeanalyzer.security.JwtTokenProvider;
import com.example.resumeanalyzer.security.UserPrincipal;
import com.example.resumeanalyzer.user.entity.User;
import com.example.resumeanalyzer.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResult register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account with this email address already exists");
        }

        User user = new User(
                request.getName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.getPassword())
        );
        user = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = refreshTokenService.createRefreshSession(user);

        UserProfileResponse profile = new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
        AuthResponse response = new AuthResponse(accessToken, tokenProvider.getAccessTokenExpirationMs(), profile);

        return new AuthResult(response, refreshToken);
    }

    @Transactional
    public AuthResult login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = refreshTokenService.createRefreshSession(user);

        UserProfileResponse profile = new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
        AuthResponse response = new AuthResponse(accessToken, tokenProvider.getAccessTokenExpirationMs(), profile);

        return new AuthResult(response, refreshToken);
    }

    @Transactional
    public AuthResult refresh(String currentRefreshToken) {
        RefreshTokenService.RefreshResult result = refreshTokenService.rotateRefreshSession(currentRefreshToken);
        User user = result.user();

        UserPrincipal principal = UserPrincipal.create(user);
        String newAccessToken = tokenProvider.generateAccessToken(principal);

        UserProfileResponse profile = new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
        AuthResponse response = new AuthResponse(newAccessToken, tokenProvider.getAccessTokenExpirationMs(), profile);

        return new AuthResult(response, result.newRawToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeSession(refreshToken);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password cannot be the same as the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Revoke all existing sessions to enforce re-login with the new password
        refreshTokenService.revokeAllSessionsForUser(user);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        refreshTokenService.revokeAllSessionsForUser(user);
        userRepository.delete(user);
    }

    public record AuthResult(AuthResponse response, String refreshToken) {}
}
