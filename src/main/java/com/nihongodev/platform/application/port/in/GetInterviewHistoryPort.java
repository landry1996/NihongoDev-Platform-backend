package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.InterviewSessionDto;

import java.util.List;
import java.util.UUID;

public interface GetInterviewHistoryPort {
    List<InterviewSessionDto> getUserHistory(UUID userId);
    InterviewSessionDto getSessionById(UUID userId, UUID sessionId);
}
