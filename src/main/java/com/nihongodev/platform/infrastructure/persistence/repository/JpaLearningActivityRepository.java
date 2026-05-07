package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.LearningActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaLearningActivityRepository extends JpaRepository<LearningActivityEntity, UUID> {
    Page<LearningActivityEntity> findByUserIdOrderByOccurredAtDesc(UUID userId, Pageable pageable);
    List<LearningActivityEntity> findByUserIdAndOccurredAtAfter(UUID userId, LocalDateTime after);
    boolean existsByUserIdAndReferenceIdAndActivityType(UUID userId, UUID referenceId, String activityType);

    @Query("SELECT COUNT(DISTINCT CAST(a.occurredAt AS date)) FROM LearningActivityEntity a WHERE a.userId = :userId AND a.occurredAt > :after")
    long countDistinctDaysActiveAfter(@Param("userId") UUID userId, @Param("after") LocalDateTime after);

    @Query("SELECT DISTINCT a.userId FROM LearningActivityEntity a WHERE a.occurredAt > :since")
    List<UUID> findDistinctUserIdsWithActivitySince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT a.userId) FROM LearningActivityEntity a WHERE a.occurredAt > :since")
    long countDistinctUsersActiveSince(@Param("since") LocalDateTime since);
}
