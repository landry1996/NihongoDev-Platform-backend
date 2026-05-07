package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.Trend;
import java.util.List;

public record UserStatisticsDto(
        double averageScore7Days,
        double averageScore30Days,
        double averageScoreAllTime,
        double learningVelocity,
        double consistencyRate,
        Trend progressTrend,
        List<WeakAreaDto> weakAreas,
        List<RecommendationDto> recommendations
) {}
