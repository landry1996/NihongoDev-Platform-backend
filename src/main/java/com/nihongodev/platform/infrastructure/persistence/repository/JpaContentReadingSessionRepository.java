package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.ContentReadingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaContentReadingSessionRepository extends JpaRepository<ContentReadingSessionEntity, UUID> {

    List<ContentReadingSessionEntity> findByUserIdOrderByStartedAtDesc(UUID userId);

    List<ContentReadingSessionEntity> findByUserIdAndContentId(UUID userId, UUID contentId);

    @Query("SELECT s FROM ContentReadingSessionEntity s WHERE s.userId = :userId " +
           "AND s.contentId = :contentId AND s.completed = false")
    Optional<ContentReadingSessionEntity> findActiveByUserIdAndContentId(
        @Param("userId") UUID userId, @Param("contentId") UUID contentId);
}
