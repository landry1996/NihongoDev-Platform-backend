package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.InterviewAnswer;

import java.util.List;
import java.util.UUID;

public interface InterviewAnswerRepositoryPort {
    InterviewAnswer save(InterviewAnswer answer);
    List<InterviewAnswer> findBySessionId(UUID sessionId);
}
