package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CodeJapaneseProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCodeJapaneseProgressRepository extends JpaRepository<CodeJapaneseProgressEntity, UUID> {
    Optional<CodeJapaneseProgressEntity> findByUserIdAndExerciseType(UUID userId, String exerciseType);
    List<CodeJapaneseProgressEntity> findByUserId(UUID userId);
}
