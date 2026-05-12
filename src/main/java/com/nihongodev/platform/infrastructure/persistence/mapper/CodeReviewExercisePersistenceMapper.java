package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.CodeReviewExerciseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CodeReviewExercisePersistenceMapper {

    public CodeReviewExercise toDomain(CodeReviewExerciseEntity entity) {
        CodeReviewExercise exercise = new CodeReviewExercise();
        exercise.setId(entity.getId());
        exercise.setTitle(entity.getTitle());
        exercise.setTitleJp(entity.getTitleJp());
        exercise.setType(ExerciseType.valueOf(entity.getExerciseType()));
        exercise.setCodeContext(CodeContext.valueOf(entity.getCodeContext()));
        exercise.setDifficulty(JapaneseLevel.valueOf(entity.getDifficulty()));
        exercise.setCodeSnippet(entity.getCodeSnippet());
        exercise.setCodeLanguage(entity.getCodeLanguage());
        exercise.setScenario(entity.getScenario());
        exercise.setScenarioJp(entity.getScenarioJp());
        if (entity.getExpectedReviewLevel() != null) {
            exercise.setExpectedReviewLevel(ReviewLevel.valueOf(entity.getExpectedReviewLevel()));
        }
        exercise.setTechnicalIssues(entity.getTechnicalIssues());
        exercise.setModelAnswer(entity.getModelAnswer());
        exercise.setModelAnswerExplanation(entity.getModelAnswerExplanation());
        exercise.setKeyPhrases(entity.getKeyPhrases());
        exercise.setAvoidPhrases(entity.getAvoidPhrases());
        exercise.setTechnicalTermsJp(entity.getTechnicalTermsJp());
        exercise.setCulturalNote(entity.getCulturalNote());
        exercise.setXpReward(entity.getXpReward());
        exercise.setPublished(entity.isPublished());
        exercise.setCreatedAt(entity.getCreatedAt());
        return exercise;
    }

    public CodeReviewExerciseEntity toEntity(CodeReviewExercise domain) {
        CodeReviewExerciseEntity entity = new CodeReviewExerciseEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setTitleJp(domain.getTitleJp());
        entity.setExerciseType(domain.getType().name());
        entity.setCodeContext(domain.getCodeContext().name());
        entity.setDifficulty(domain.getDifficulty().name());
        entity.setCodeSnippet(domain.getCodeSnippet());
        entity.setCodeLanguage(domain.getCodeLanguage());
        entity.setScenario(domain.getScenario());
        entity.setScenarioJp(domain.getScenarioJp());
        if (domain.getExpectedReviewLevel() != null) {
            entity.setExpectedReviewLevel(domain.getExpectedReviewLevel().name());
        }
        entity.setTechnicalIssues(domain.getTechnicalIssues());
        entity.setModelAnswer(domain.getModelAnswer());
        entity.setModelAnswerExplanation(domain.getModelAnswerExplanation());
        entity.setKeyPhrases(domain.getKeyPhrases());
        entity.setAvoidPhrases(domain.getAvoidPhrases());
        entity.setTechnicalTermsJp(domain.getTechnicalTermsJp());
        entity.setCulturalNote(domain.getCulturalNote());
        entity.setXpReward(domain.getXpReward());
        entity.setPublished(domain.isPublished());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
