package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.UserStatistics;
import com.nihongodev.platform.infrastructure.persistence.mapper.UserStatisticsPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaLearningActivityRepository;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserStatisticsRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StatisticsRepositoryAdapter implements StatisticsRepositoryPort {

    private final JpaUserStatisticsRepository jpaRepository;
    private final JpaLearningActivityRepository activityRepository;
    private final UserStatisticsPersistenceMapper mapper;

    public StatisticsRepositoryAdapter(JpaUserStatisticsRepository jpaRepository,
                                       JpaLearningActivityRepository activityRepository,
                                       UserStatisticsPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.activityRepository = activityRepository;
        this.mapper = mapper;
    }

    @Override
    public UserStatistics save(UserStatistics statistics) {
        var entity = mapper.toEntity(statistics);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserStatistics> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<UUID> findUserIdsWithActivitySince(LocalDateTime since) {
        return activityRepository.findDistinctUserIdsWithActivitySince(since);
    }
}
