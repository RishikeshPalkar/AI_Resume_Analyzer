package com.example.resumeanalyzer.auth.service;

import com.example.resumeanalyzer.common.exception.UnauthorizedException;
import com.example.resumeanalyzer.user.entity.RefreshSession;
import com.example.resumeanalyzer.user.entity.User;
import com.example.resumeanalyzer.user.repository.RefreshSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshSessionRepository refreshSessionRepository;
    private final long refreshTokenExpirationMs;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshSessionRepository refreshSessionRepository,
            @Value("${app.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationMs) {
        this.refreshSessionRepository = refreshSessionRepository;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Transactional
    public String createRefreshSession(User user) {
        String rawToken = generateSecureRandomToken();
        String tokenHash = hashToken(rawToken);
        Instant expiryDate = Instant.now().plusMillis(refreshTokenExpirationMs);

        RefreshSession session = new RefreshSession(user, tokenHash, expiryDate);
        refreshSessionRepository.save(session);

        return rawToken;
    }

    @Transactional
    public RefreshResult rotateRefreshSession(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }

        String tokenHash = hashToken(rawToken);
        RefreshSession session = refreshSessionRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (session.getRevokedAt() != null) {
            // Possible token reuse attack detected! Invalidate all sessions for this user for security
            refreshSessionRepository.revokeAllActiveSessionsForUser(session.getUser(), Instant.now());
            throw new UnauthorizedException("Refresh token reuse detected. All sessions have been revoked.");
        }

        if (session.getExpiresAt().isBefore(Instant.now())) {
            session.setRevokedAt(Instant.now());
            refreshSessionRepository.save(session);
            throw new UnauthorizedException("Refresh token has expired. Please log in again.");
        }

        // Revoke current session
        session.setRevokedAt(Instant.now());
        refreshSessionRepository.save(session);

        // Issue new rotated session
        User user = session.getUser();
        String newRawToken = generateSecureRandomToken();
        String newTokenHash = hashToken(newRawToken);
        Instant newExpiry = Instant.now().plusMillis(refreshTokenExpirationMs);

        RefreshSession newSession = new RefreshSession(user, newTokenHash, newExpiry);
        newSession.setRotatedFromSessionId(session.getId());
        refreshSessionRepository.save(newSession);

        return new RefreshResult(user, newRawToken);
    }

    @Transactional
    public void revokeSession(String rawToken) {
        if (rawToken != null && !rawToken.isBlank()) {
            String tokenHash = hashToken(rawToken);
            refreshSessionRepository.findByTokenHash(tokenHash).ifPresent(session -> {
                session.setRevokedAt(Instant.now());
                refreshSessionRepository.save(session);
            });
        }
    }

    @Transactional
    public void revokeAllSessionsForUser(User user) {
        refreshSessionRepository.revokeAllActiveSessionsForUser(user, Instant.now());
    }

    private String generateSecureRandomToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public record RefreshResult(User user, String newRawToken) {}
}
