package com.example.resumeanalyzer.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // 256-bit

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, 3600000L, "ai-resume-analyzer-test");
    }

    @Test
    @DisplayName("Should generate a valid JWT and parse user ID correctly")
    void testGenerateAndValidateToken() {
        UserPrincipal principal = new UserPrincipal(
                42L, "Jane Doe", "jane@example.com", "secret", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = tokenProvider.generateAccessToken(principal);
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));

        Long userId = tokenProvider.getUserIdFromToken(token);
        assertEquals(42L, userId);
    }

    @Test
    @DisplayName("Should reject malformed or tampered token")
    void testTamperedToken() {
        UserPrincipal principal = new UserPrincipal(
                1L, "Test", "test@example.com", "pass", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = tokenProvider.generateAccessToken(principal);
        String tamperedToken = token + "corrupted";

        assertFalse(tokenProvider.validateToken(tamperedToken));
    }

    @Test
    @DisplayName("Should reject expired token")
    void testExpiredToken() {
        // 1 ms expiration
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(secret, 1L, "ai-resume-analyzer-test");
        UserPrincipal principal = new UserPrincipal(
                1L, "Test", "test@example.com", "pass", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = shortLivedProvider.generateAccessToken(principal);

        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {}

        assertFalse(shortLivedProvider.validateToken(token));
    }
}
