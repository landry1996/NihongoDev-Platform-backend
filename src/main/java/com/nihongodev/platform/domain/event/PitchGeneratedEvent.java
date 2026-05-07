package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PitchGeneratedEvent(
        UUID eventId,
        String eventType,
        UUID userId,
        LocalDateTime occurredAt,
        UUID pitchId,
        String pitchType
) implements DomainEvent {

    public static PitchGeneratedEvent of(UUID userId, UUID pitchId, String pitchType) {
        return new PitchGeneratedEvent(UUID.randomUUID(), "PITCH_GENERATED", userId, LocalDateTime.now(),
                pitchId, pitchType);
    }
}
