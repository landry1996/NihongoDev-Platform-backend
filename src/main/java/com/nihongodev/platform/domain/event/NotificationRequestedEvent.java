package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationRequestedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        String notificationType,
        UUID targetEntityId,
        String message
) implements DomainEvent {

    public static NotificationRequestedEvent of(UUID userId, String notificationType,
                                                 UUID targetEntityId, String message) {
        return new NotificationRequestedEvent(UUID.randomUUID(), "NOTIFICATION_REQUESTED", userId,
                LocalDateTime.now(), notificationType, targetEntityId, message);
    }
}
