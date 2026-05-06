package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.QuizResultDto;

import java.util.List;
import java.util.UUID;

public interface GetQuizHistoryPort {
    List<QuizResultDto> getUserHistory(UUID userId);
    QuizResultDto getResultByAttempt(UUID attemptId);
}
