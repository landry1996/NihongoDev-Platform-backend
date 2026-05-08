package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.*;

import java.util.List;

public record CreateScenarioCommand(
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
        String modelAnswer,
        String modelAnswerExplanation,
        List<String> keyPhrases,
        List<String> avoidPhrases,
        String culturalNote,
        int xpReward
) {}
