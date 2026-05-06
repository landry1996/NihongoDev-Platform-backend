package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.Lesson;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonRepositoryPort {
    Lesson save(Lesson lesson);
    Optional<Lesson> findById(UUID id);
    List<Lesson> findAll();
    List<Lesson> findByType(LessonType type);
    List<Lesson> findByLevel(LessonLevel level);
    List<Lesson> findByTypeAndLevel(LessonType type, LessonLevel level);
    List<Lesson> findPublished();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
