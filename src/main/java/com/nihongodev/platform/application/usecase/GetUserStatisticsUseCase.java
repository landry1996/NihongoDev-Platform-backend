package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.RecommendationDto;
import com.nihongodev.platform.application.dto.UserStatisticsDto;
import com.nihongodev.platform.application.dto.WeakAreaDto;
import com.nihongodev.platform.application.port.in.GetUserStatisticsPort;
import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.UserStatistics;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetUserStatisticsUseCase implements GetUserStatisticsPort {

    private final StatisticsRepositoryPort statisticsRepository;

    public GetUserStatisticsUseCase(StatisticsRepositoryPort statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public UserStatisticsDto execute(UUID userId) {
        UserStatistics stats = statisticsRepository.findByUserId(userId)
                .orElseGet(() -> UserStatistics.initialize(userId));

        List<WeakAreaDto> weakAreas = stats.getWeakAreas().stream()
                .map(w -> new WeakAreaDto(w.getModuleType(), w.getTopic(), w.getAverageScore(), w.getPriority()))
                .toList();

        List<RecommendationDto> recommendations = stats.getRecommendations().stream()
                .map(r -> new RecommendationDto(r.getType(), r.getTargetId(), r.getReason(), r.getPriority()))
                .toList();

        return new UserStatisticsDto(
                stats.getAverageScore7Days(),
                stats.getAverageScore30Days(),
                stats.getAverageScoreAllTime(),
                stats.getLearningVelocity(),
                stats.getConsistencyRate(),
                stats.getProgressTrend(),
                weakAreas,
                recommendations
        );
    }
}
