package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.*;

import java.util.List;
import java.util.UUID;

public record CulturalScenarioDto(
        UUID id,
        String title,
        String titleJp,
        String situation,
        String situationJp,
        WorkplaceContext context,
        RelationshipType relationship,
        ScenarioMode mode,
        ScenarioCategory category,
        KeigoLevel expectedKeigoLevel,
        JapaneseLevel difficulty,
        List<ScenarioChoice> choices,
        String culturalNote,
        int xpReward
) {}
