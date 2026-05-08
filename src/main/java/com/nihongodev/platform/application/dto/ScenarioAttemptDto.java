package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.CulturalScore;
import com.nihongodev.platform.domain.model.KeigoViolation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ScenarioAttemptDto(
        UUID id,
        UUID scenarioId,
        String scenarioTitle,
        String userResponse,
        CulturalScore score,
        List<KeigoViolation> violations,
        String feedback,
        String modelAnswer,
        String modelAnswerExplanation,
        int timeSpentSeconds,
        LocalDateTime attemptedAt
) {}
