package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.ScenarioCategory;

import java.time.LocalDateTime;

public record CulturalProgressDto(
        ScenarioCategory category,
        int scenariosCompleted,
        int averageScore,
        int bestScore,
        int currentStreak,
        KeigoLevel unlockedLevel,
        LocalDateTime lastActivityAt
) {}
