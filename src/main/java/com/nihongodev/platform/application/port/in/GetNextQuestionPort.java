package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.InterviewQuestionDto;

import java.util.UUID;

public interface GetNextQuestionPort {
    InterviewQuestionDto getNextQuestion(UUID userId, UUID sessionId);
}
