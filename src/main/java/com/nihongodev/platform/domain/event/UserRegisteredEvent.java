package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        String firstName,
        String role,
        LocalDateTime occurredAt
) {
    public static UserRegisteredEvent of(UUID userId, String email, String firstName, String role) {
        return new UserRegisteredEvent(userId, email, firstName, role, LocalDateTime.now());
    }
}
