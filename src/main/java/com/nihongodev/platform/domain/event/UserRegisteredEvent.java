package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        String email,
        String firstName,
        String role
) implements DomainEvent {

    public static UserRegisteredEvent of(UUID userId, String email, String firstName, String role) {
        return new UserRegisteredEvent(UUID.randomUUID(), "USER_REGISTERED", userId, LocalDateTime.now(),
                email, firstName, role);
    }
}
