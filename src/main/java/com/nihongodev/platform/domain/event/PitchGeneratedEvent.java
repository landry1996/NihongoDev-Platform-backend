package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.PitchType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PitchGeneratedEvent(
        UUID userId,
        UUID pitchId,
        String pitchType,
        LocalDateTime generatedAt
) {
    public static PitchGeneratedEvent of(UUID userId, UUID pitchId, PitchType type) {
        return new PitchGeneratedEvent(userId, pitchId, type.name(), LocalDateTime.now());
    }
}
