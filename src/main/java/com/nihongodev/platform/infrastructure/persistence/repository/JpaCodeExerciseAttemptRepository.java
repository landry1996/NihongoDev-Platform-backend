package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CodeExerciseAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCodeExerciseAttemptRepository extends JpaRepository<CodeExerciseAttemptEntity, UUID> {
    List<CodeExerciseAttemptEntity> findByUserId(UUID userId);
    List<CodeExerciseAttemptEntity> findByUserIdAndExerciseId(UUID userId, UUID exerciseId);
}
