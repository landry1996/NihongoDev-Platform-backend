package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.ScenarioCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScenarioCompletedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        UUID scenarioId,
        int overallScore,
        ScenarioCategory category,
        LocalDateTime occurredAt
) implements DomainEvent {

    public static ScenarioCompletedEvent create(UUID userId, UUID scenarioId, int overallScore, ScenarioCategory category) {
        return new ScenarioCompletedEvent(
                UUID.randomUUID(), "SCENARIO_COMPLETED", userId, scenarioId, overallScore, category, LocalDateTime.now()
        );
    }
}
