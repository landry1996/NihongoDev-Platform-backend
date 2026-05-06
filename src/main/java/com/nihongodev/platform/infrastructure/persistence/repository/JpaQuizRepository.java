package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaQuizRepository extends JpaRepository<QuizEntity, UUID> {
    List<QuizEntity> findByLessonId(UUID lessonId);
    List<QuizEntity> findByPublishedTrue();
}
