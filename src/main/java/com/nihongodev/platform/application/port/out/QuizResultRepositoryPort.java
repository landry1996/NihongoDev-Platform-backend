package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.QuizResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizResultRepositoryPort {
    QuizResult save(QuizResult result);
    Optional<QuizResult> findByAttemptId(UUID attemptId);
    List<QuizResult> findByUserId(UUID userId);
    List<QuizResult> findByQuizId(UUID quizId);
}
