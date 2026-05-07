package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.Priority;
import com.nihongodev.platform.domain.model.RecommendationType;
import java.util.UUID;

public record RecommendationDto(
        RecommendationType type,
        UUID targetId,
        String reason,
        Priority priority
) {}
