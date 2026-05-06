package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record TextCorrectedEvent(
        UUID userId,
        UUID sessionId,
        String textType,
        double overallScore,
        int totalAnnotations,
        int errorCount,
        LocalDateTime correctedAt
) {
    public static TextCorrectedEvent of(UUID userId, UUID sessionId, String textType,
                                        double overallScore, int totalAnnotations,
                                        int errorCount, LocalDateTime correctedAt) {
        return new TextCorrectedEvent(userId, sessionId, textType, overallScore,
                totalAnnotations, errorCount, correctedAt);
    }
}
