package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.QuizAttempt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizAttemptRepositoryPort {
    QuizAttempt save(QuizAttempt attempt);
    Optional<QuizAttempt> findById(UUID id);
    List<QuizAttempt> findByUserIdAndQuizId(UUID userId, UUID quizId);
    List<QuizAttempt> findByUserId(UUID userId);
}
