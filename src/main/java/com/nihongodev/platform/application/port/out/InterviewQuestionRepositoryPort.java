package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.InterviewDifficulty;
import com.nihongodev.platform.domain.model.InterviewQuestion;
import com.nihongodev.platform.domain.model.InterviewType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewQuestionRepositoryPort {
    InterviewQuestion save(InterviewQuestion question);
    Optional<InterviewQuestion> findById(UUID id);
    List<InterviewQuestion> findByTypeAndDifficulty(InterviewType type, InterviewDifficulty difficulty);
}
