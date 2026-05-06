package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.Quiz;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRepositoryPort {
    Quiz save(Quiz quiz);
    Optional<Quiz> findById(UUID id);
    List<Quiz> findByLessonId(UUID lessonId);
    List<Quiz> findPublished();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
