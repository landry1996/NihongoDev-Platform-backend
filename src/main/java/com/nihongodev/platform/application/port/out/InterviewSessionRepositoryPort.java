package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.InterviewSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewSessionRepositoryPort {
    InterviewSession save(InterviewSession session);
    Optional<InterviewSession> findById(UUID id);
    List<InterviewSession> findByUserId(UUID userId);
}
