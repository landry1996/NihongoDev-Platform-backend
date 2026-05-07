package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.application.port.out.AnalyticsQueryPort;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.domain.model.ProgressLevel;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaLearningActivityRepository;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserProgressRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AnalyticsQueryAdapter implements AnalyticsQueryPort {

    private final JpaUserProgressRepository progressRepository;
    private final JpaLearningActivityRepository activityRepository;

    public AnalyticsQueryAdapter(JpaUserProgressRepository progressRepository,
                                 JpaLearningActivityRepository activityRepository) {
        this.progressRepository = progressRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public long countActiveUsers(LocalDateTime since) {
        return activityRepository.countDistinctUsersActiveSince(since);
    }

    @Override
    public double averageGlobalScore() {
        return progressRepository.findAll().stream()
                .mapToDouble(e -> e.getGlobalScore())
                .average().orElse(0);
    }

    @Override
    public Map<ModuleType, Double> averageScoreByModule() {
        return new HashMap<>();
    }

    @Override
    public long countTotalActivities() {
        return activityRepository.count();
    }

    @Override
    public List<TopUserDto> topUsers(int limit) {
        return progressRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "totalXp"))).stream()
                .map(e -> new TopUserDto(
                        e.getUserId(), null, e.getTotalXp(), e.getGlobalScore(),
                        ProgressLevel.valueOf(e.getLevel())))
                .toList();
    }
}
