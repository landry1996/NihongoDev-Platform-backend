package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.InterviewSessionDto;

import java.util.UUID;

public interface CompleteInterviewPort {
    InterviewSessionDto complete(UUID userId, UUID sessionId);
}
