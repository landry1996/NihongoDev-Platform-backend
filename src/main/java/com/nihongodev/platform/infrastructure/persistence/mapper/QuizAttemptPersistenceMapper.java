package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.AttemptStatus;
import com.nihongodev.platform.domain.model.QuizAttempt;
import com.nihongodev.platform.domain.model.QuizMode;
import com.nihongodev.platform.infrastructure.persistence.entity.QuizAttemptEntity;
import org.springframework.stereotype.Component;

@Component
public class QuizAttemptPersistenceMapper {

    public QuizAttempt toDomain(QuizAttemptEntity entity) {
        if (entity == null) return null;
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(entity.getId());
        attempt.setQuizId(entity.getQuizId());
        attempt.setUserId(entity.getUserId());
        attempt.setMode(entity.getMode() != null ? QuizMode.valueOf(entity.getMode()) : QuizMode.CLASSIC);
        attempt.setStatus(entity.getStatus() != null ? AttemptStatus.valueOf(entity.getStatus()) : AttemptStatus.IN_PROGRESS);
        attempt.setCurrentStreak(entity.getCurrentStreak());
        attempt.setMaxStreak(entity.getMaxStreak());
        attempt.setLivesRemaining(entity.getLivesRemaining());
        attempt.setStartedAt(entity.getStartedAt());
        attempt.setCompletedAt(entity.getCompletedAt());
        attempt.setTimeSpentSeconds(entity.getTimeSpentSeconds());
        return attempt;
    }

    public QuizAttemptEntity toEntity(QuizAttempt attempt) {
        if (attempt == null) return null;
        QuizAttemptEntity entity = new QuizAttemptEntity();
        entity.setId(attempt.getId());
        entity.setQuizId(attempt.getQuizId());
        entity.setUserId(attempt.getUserId());
        entity.setMode(attempt.getMode() != null ? attempt.getMode().name() : null);
        entity.setStatus(attempt.getStatus() != null ? attempt.getStatus().name() : null);
        entity.setCurrentStreak(attempt.getCurrentStreak());
        entity.setMaxStreak(attempt.getMaxStreak());
        entity.setLivesRemaining(attempt.getLivesRemaining());
        entity.setStartedAt(attempt.getStartedAt());
        entity.setCompletedAt(attempt.getCompletedAt());
        entity.setTimeSpentSeconds(attempt.getTimeSpentSeconds());
        return entity;
    }
}
