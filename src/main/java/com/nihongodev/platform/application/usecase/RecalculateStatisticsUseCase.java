package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.RecalculateStatisticsPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.LearningActivity;
import com.nihongodev.platform.domain.model.UserStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecalculateStatisticsUseCase implements RecalculateStatisticsPort {

    private static final Logger log = LoggerFactory.getLogger(RecalculateStatisticsUseCase.class);

    private final StatisticsRepositoryPort statisticsRepository;
    private final LearningActivityRepositoryPort activityRepository;

    public RecalculateStatisticsUseCase(StatisticsRepositoryPort statisticsRepository,
                                        LearningActivityRepositoryPort activityRepository) {
        this.statisticsRepository = statisticsRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    @Transactional
    public void recalculateAll() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(20);
        List<UUID> activeUserIds = statisticsRepository.findUserIdsWithActivitySince(since);

        log.info("Recalculating statistics for {} active users", activeUserIds.size());

        for (UUID userId : activeUserIds) {
            recalculateForUser(userId);
        }
    }

    private void recalculateForUser(UUID userId) {
        UserStatistics stats = statisticsRepository.findByUserId(userId)
                .orElseGet(() -> UserStatistics.initialize(userId));

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<LearningActivity> activities = activityRepository
                .findByUserIdAndOccurredAfter(userId, thirtyDaysAgo);

        long daysActive7 = activityRepository.countDistinctDaysActiveAfter(
                userId, LocalDateTime.now().minusDays(7));
        long daysActive30 = activityRepository.countDistinctDaysActiveAfter(
                userId, thirtyDaysAgo);

        stats.recalculate(activities, (int) daysActive7, (int) daysActive30);
        statisticsRepository.save(stats);
    }
}
