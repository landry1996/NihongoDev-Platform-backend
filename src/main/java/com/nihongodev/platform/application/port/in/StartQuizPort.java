package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.QuizAttemptDto;
import com.nihongodev.platform.domain.model.QuizMode;

import java.util.UUID;

public interface StartQuizPort {
    QuizAttemptDto start(UUID userId, UUID quizId, QuizMode mode);
}
