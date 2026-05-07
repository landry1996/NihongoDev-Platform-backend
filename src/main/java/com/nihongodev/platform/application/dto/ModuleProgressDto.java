package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ModuleStatus;
import com.nihongodev.platform.domain.model.ModuleType;
import java.time.LocalDateTime;

public record ModuleProgressDto(
        ModuleType moduleType,
        int completedItems,
        Integer totalItems,
        double completionPercentage,
        double averageScore,
        double bestScore,
        ModuleStatus status,
        LocalDateTime lastCompletedAt
) {}
