package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record BadgeEarnedEvent(
    UUID eventId,
    String eventType,
    UUID userId,
    UUID badgeId,
    String badgeCode,
    String badgeNameJp,
    int xpReward,
    LocalDateTime occurredAt
) implements DomainEvent {
    public static BadgeEarnedEvent create(UUID userId, UUID badgeId, String code, String nameJp, int xpReward) {
        return new BadgeEarnedEvent(
            UUID.randomUUID(), "BADGE_EARNED", userId, badgeId, code, nameJp, xpReward, LocalDateTime.now()
        );
    }
}
