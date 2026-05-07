package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.ProgressLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgressUpdatedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        long totalXp,
        ProgressLevel level,
        double globalScore,
        ActivityType activityType
) implements DomainEvent {

    public static ProgressUpdatedEvent of(UUID userId, long totalXp, ProgressLevel level,
                                          double globalScore, ActivityType activityType) {
        return new ProgressUpdatedEvent(UUID.randomUUID(), "PROGRESS_UPDATED", userId, LocalDateTime.now(),
                totalXp, level, globalScore, activityType);
    }
}
