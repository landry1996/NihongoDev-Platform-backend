package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.ScenarioAttemptEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScenarioAttemptPersistenceMapper {

    private final ObjectMapper objectMapper;

    public ScenarioAttemptPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ScenarioAttempt toDomain(ScenarioAttemptEntity entity) {
        if (entity == null) return null;
        ScenarioAttempt attempt = new ScenarioAttempt();
        attempt.setId(entity.getId());
        attempt.setUserId(entity.getUserId());
        attempt.setScenarioId(entity.getScenarioId());
        attempt.setUserResponse(entity.getUserResponse());
        attempt.setSelectedChoiceId(entity.getSelectedChoiceId());
        attempt.setScore(new CulturalScore(
                entity.getKeigoScore(),
                entity.getAppropriatenessScore(),
                entity.getUchiSotoScore(),
                entity.getIndirectnessScore(),
                entity.getProfessionalToneScore(),
                entity.getOverallScore()
        ));
        attempt.setViolations(deserializeViolations(entity.getViolations()));
        attempt.setFeedback(entity.getFeedback());
        attempt.setTimeSpentSeconds(entity.getTimeSpentSeconds());
        attempt.setAttemptedAt(entity.getAttemptedAt());
        return attempt;
    }

    public ScenarioAttemptEntity toEntity(ScenarioAttempt domain) {
        if (domain == null) return null;
        ScenarioAttemptEntity entity = new ScenarioAttemptEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setScenarioId(domain.getScenarioId());
        entity.setUserResponse(domain.getUserResponse());
        entity.setSelectedChoiceId(domain.getSelectedChoiceId());
        if (domain.getScore() != null) {
            entity.setKeigoScore(domain.getScore().keigoScore());
            entity.setAppropriatenessScore(domain.getScore().appropriatenessScore());
            entity.setUchiSotoScore(domain.getScore().uchiSotoScore());
            entity.setIndirectnessScore(domain.getScore().indirectnessScore());
            entity.setProfessionalToneScore(domain.getScore().professionalToneScore());
            entity.setOverallScore(domain.getScore().overallScore());
        }
        entity.setViolations(serializeViolations(domain.getViolations()));
        entity.setFeedback(domain.getFeedback());
        entity.setTimeSpentSeconds(domain.getTimeSpentSeconds());
        entity.setAttemptedAt(domain.getAttemptedAt());
        return entity;
    }

    private String serializeViolations(List<KeigoViolation> violations) {
        try { return objectMapper.writeValueAsString(violations != null ? violations : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<KeigoViolation> deserializeViolations(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<List<KeigoViolation>>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
