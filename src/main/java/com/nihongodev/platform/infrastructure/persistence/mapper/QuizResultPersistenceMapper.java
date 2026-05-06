package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.DifficultyLevel;
import com.nihongodev.platform.domain.model.QuizResult;
import com.nihongodev.platform.infrastructure.persistence.entity.QuizResultEntity;
import org.springframework.stereotype.Component;

@Component
public class QuizResultPersistenceMapper {

    public QuizResult toDomain(QuizResultEntity entity) {
        if (entity == null) return null;
        QuizResult result = new QuizResult();
        result.setId(entity.getId());
        result.setAttemptId(entity.getAttemptId());
        result.setQuizId(entity.getQuizId());
        result.setUserId(entity.getUserId());
        result.setTotalQuestions(entity.getTotalQuestions());
        result.setCorrectAnswers(entity.getCorrectAnswers());
        result.setTotalScore(entity.getTotalScore());
        result.setMaxPossibleScore(entity.getMaxPossibleScore());
        result.setPercentage(entity.getPercentage());
        result.setPassed(entity.isPassed());
        result.setMaxStreak(entity.getMaxStreak());
        result.setAverageTimePerQuestion(entity.getAverageTimePerQuestion());
        result.setDifficultyReached(entity.getDifficultyReached() != null ? DifficultyLevel.valueOf(entity.getDifficultyReached()) : DifficultyLevel.MEDIUM);
        result.setCompletedAt(entity.getCompletedAt());
        return result;
    }

    public QuizResultEntity toEntity(QuizResult result) {
        if (result == null) return null;
        QuizResultEntity entity = new QuizResultEntity();
        entity.setId(result.getId());
        entity.setAttemptId(result.getAttemptId());
        entity.setQuizId(result.getQuizId());
        entity.setUserId(result.getUserId());
        entity.setTotalQuestions(result.getTotalQuestions());
        entity.setCorrectAnswers(result.getCorrectAnswers());
        entity.setTotalScore(result.getTotalScore());
        entity.setMaxPossibleScore(result.getMaxPossibleScore());
        entity.setPercentage(result.getPercentage());
        entity.setPassed(result.isPassed());
        entity.setMaxStreak(result.getMaxStreak());
        entity.setAverageTimePerQuestion(result.getAverageTimePerQuestion());
        entity.setDifficultyReached(result.getDifficultyReached() != null ? result.getDifficultyReached().name() : null);
        entity.setCompletedAt(result.getCompletedAt());
        return entity;
    }
}
