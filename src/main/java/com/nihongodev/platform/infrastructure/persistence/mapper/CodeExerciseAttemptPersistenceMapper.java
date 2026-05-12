package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.CodeExerciseAttemptEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CodeExerciseAttemptPersistenceMapper {

    private final ObjectMapper objectMapper;

    public CodeExerciseAttemptPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CodeExerciseAttempt toDomain(CodeExerciseAttemptEntity entity) {
        CodeExerciseAttempt attempt = new CodeExerciseAttempt();
        attempt.setId(entity.getId());
        attempt.setUserId(entity.getUserId());
        attempt.setExerciseId(entity.getExerciseId());
        attempt.setExerciseType(ExerciseType.valueOf(entity.getExerciseType()));
        attempt.setUserResponse(entity.getUserResponse());
        attempt.setScore(new CodeExerciseScore(
            entity.getTechnicalAccuracyScore(), entity.getJapaneseQualityScore(),
            entity.getProfessionalToneScore(), entity.getStructureScore(),
            entity.getTeamCommunicationScore(), entity.getOverallScore()
        ));
        if (entity.getViolations() != null) {
            try {
                List<TechnicalJapaneseViolation> violations = objectMapper.readValue(
                    entity.getViolations(), new TypeReference<>() {});
                attempt.setViolations(violations);
            } catch (Exception e) {
                attempt.setViolations(List.of());
            }
        }
        attempt.setFeedback(entity.getFeedback());
        attempt.setTimeSpentSeconds(entity.getTimeSpentSeconds());
        attempt.setAttemptedAt(entity.getAttemptedAt());
        return attempt;
    }

    public CodeExerciseAttemptEntity toEntity(CodeExerciseAttempt domain) {
        CodeExerciseAttemptEntity entity = new CodeExerciseAttemptEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setExerciseId(domain.getExerciseId());
        entity.setExerciseType(domain.getExerciseType().name());
        entity.setUserResponse(domain.getUserResponse());
        if (domain.getScore() != null) {
            entity.setTechnicalAccuracyScore(domain.getScore().technicalAccuracyScore());
            entity.setJapaneseQualityScore(domain.getScore().japaneseQualityScore());
            entity.setProfessionalToneScore(domain.getScore().professionalToneScore());
            entity.setStructureScore(domain.getScore().structureScore());
            entity.setTeamCommunicationScore(domain.getScore().teamCommunicationScore());
            entity.setOverallScore(domain.getScore().overallScore());
        }
        if (domain.getViolations() != null) {
            try {
                entity.setViolations(objectMapper.writeValueAsString(domain.getViolations()));
            } catch (Exception e) {
                entity.setViolations("[]");
            }
        }
        entity.setFeedback(domain.getFeedback());
        entity.setTimeSpentSeconds(domain.getTimeSpentSeconds());
        entity.setAttemptedAt(domain.getAttemptedAt());
        return entity;
    }
}
