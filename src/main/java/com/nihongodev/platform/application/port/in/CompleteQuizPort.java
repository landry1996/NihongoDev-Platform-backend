package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.QuizResultDto;

import java.util.UUID;

public interface CompleteQuizPort {
    QuizResultDto complete(UUID userId, UUID attemptId);
}
