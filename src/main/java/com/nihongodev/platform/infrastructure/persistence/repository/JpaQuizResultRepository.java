package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.QuizResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaQuizResultRepository extends JpaRepository<QuizResultEntity, UUID> {
    Optional<QuizResultEntity> findByAttemptId(UUID attemptId);
    List<QuizResultEntity> findByUserId(UUID userId);
    List<QuizResultEntity> findByQuizId(UUID quizId);
}
