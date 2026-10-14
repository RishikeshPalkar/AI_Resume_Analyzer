package com.example.resumeanalyzer.user.repository;

import com.example.resumeanalyzer.user.entity.RefreshSession;
import com.example.resumeanalyzer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshSessionRepository extends JpaRepository<RefreshSession, Long> {
    Optional<RefreshSession> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshSession r SET r.revokedAt = :revokedAt WHERE r.user = :user AND r.revokedAt IS NULL")
    void revokeAllActiveSessionsForUser(User user, Instant revokedAt);
}
