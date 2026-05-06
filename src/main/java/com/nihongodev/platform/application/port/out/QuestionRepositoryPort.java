package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepositoryPort {
    Question save(Question question);
    Optional<Question> findById(UUID id);
    List<Question> findByQuizId(UUID quizId);
}
