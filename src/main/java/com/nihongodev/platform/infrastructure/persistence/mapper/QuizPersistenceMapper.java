package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.Quiz;
import com.nihongodev.platform.domain.model.QuizMode;
import com.nihongodev.platform.infrastructure.persistence.entity.QuizEntity;
import org.springframework.stereotype.Component;

@Component
public class QuizPersistenceMapper {

    public Quiz toDomain(QuizEntity entity) {
        if (entity == null) return null;
        Quiz quiz = new Quiz();
        quiz.setId(entity.getId());
        quiz.setLessonId(entity.getLessonId());
        quiz.setTitle(entity.getTitle());
        quiz.setDescription(entity.getDescription());
        quiz.setLevel(entity.getLevel());
        quiz.setMode(entity.getMode() != null ? QuizMode.valueOf(entity.getMode()) : QuizMode.CLASSIC);
        quiz.setTimeLimitSeconds(entity.getTimeLimitSeconds());
        quiz.setMaxAttempts(entity.getMaxAttempts());
        quiz.setPassingScore(entity.getPassingScore());
        quiz.setPublished(entity.isPublished());
        quiz.setCreatedAt(entity.getCreatedAt());
        quiz.setUpdatedAt(entity.getUpdatedAt());
        return quiz;
    }

    public QuizEntity toEntity(Quiz quiz) {
        if (quiz == null) return null;
        QuizEntity entity = new QuizEntity();
        entity.setId(quiz.getId());
        entity.setLessonId(quiz.getLessonId());
        entity.setTitle(quiz.getTitle());
        entity.setDescription(quiz.getDescription());
        entity.setLevel(quiz.getLevel());
        entity.setMode(quiz.getMode() != null ? quiz.getMode().name() : null);
        entity.setTimeLimitSeconds(quiz.getTimeLimitSeconds());
        entity.setMaxAttempts(quiz.getMaxAttempts());
        entity.setPassingScore(quiz.getPassingScore());
        entity.setPublished(quiz.isPublished());
        entity.setCreatedAt(quiz.getCreatedAt());
        entity.setUpdatedAt(quiz.getUpdatedAt());
        return entity;
    }
}
