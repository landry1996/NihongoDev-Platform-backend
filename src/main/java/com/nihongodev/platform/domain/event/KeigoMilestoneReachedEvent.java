package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.KeigoLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record KeigoMilestoneReachedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        KeigoLevel level,
        int averageScore,
        LocalDateTime occurredAt
) implements DomainEvent {

    public static KeigoMilestoneReachedEvent create(UUID userId, KeigoLevel level, int averageScore) {
        return new KeigoMilestoneReachedEvent(
                UUID.randomUUID(), "KEIGO_MILESTONE_REACHED", userId, level, averageScore, LocalDateTime.now()
        );
    }
}
