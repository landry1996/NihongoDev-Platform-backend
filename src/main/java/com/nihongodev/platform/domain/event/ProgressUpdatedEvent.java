package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.ProgressLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgressUpdatedEvent(
        UUID userId,
        long totalXp,
        ProgressLevel level,
        double globalScore,
        ActivityType activityType,
        LocalDateTime occurredAt
) {
    public static ProgressUpdatedEvent of(UUID userId, long totalXp, ProgressLevel level,
                                          double globalScore, ActivityType activityType) {
        return new ProgressUpdatedEvent(userId, totalXp, level, globalScore, activityType, LocalDateTime.now());
    }
}
