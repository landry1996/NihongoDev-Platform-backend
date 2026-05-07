package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ActivityType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record LearningActivityDto(
        ActivityType activityType,
        UUID referenceId,
        Double score,
        int xpEarned,
        Map<String, Object> metadata,
        LocalDateTime occurredAt
) {}
