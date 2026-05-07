package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ModuleType;
import java.util.Map;

public record PlatformAnalyticsDto(
        long totalUsers,
        long activeUsersLast7Days,
        long activeUsersLast30Days,
        double averageGlobalScore,
        long totalActivities,
        Map<ModuleType, Double> averageScoreByModule
) {}
