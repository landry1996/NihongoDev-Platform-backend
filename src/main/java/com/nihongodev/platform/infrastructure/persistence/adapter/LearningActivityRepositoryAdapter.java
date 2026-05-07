package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.LearningActivity;
import com.nihongodev.platform.infrastructure.persistence.mapper.LearningActivityPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaLearningActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class LearningActivityRepositoryAdapter implements LearningActivityRepositoryPort {

    private final JpaLearningActivityRepository jpaRepository;
    private final LearningActivityPersistenceMapper mapper;

    public LearningActivityRepositoryAdapter(JpaLearningActivityRepository jpaRepository, LearningActivityPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public LearningActivity save(LearningActivity activity) {
        var entity = mapper.toEntity(activity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Page<LearningActivity> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByOccurredAtDesc(userId, pageable).map(mapper::toDomain);
    }

    @Override
    public List<LearningActivity> findByUserIdAndOccurredAfter(UUID userId, LocalDateTime after) {
        return jpaRepository.findByUserIdAndOccurredAtAfter(userId, after).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByUserIdAndReferenceIdAndActivityType(UUID userId, UUID referenceId, ActivityType activityType) {
        return jpaRepository.existsByUserIdAndReferenceIdAndActivityType(userId, referenceId, activityType.name());
    }

    @Override
    public long countDistinctDaysActiveAfter(UUID userId, LocalDateTime after) {
        return jpaRepository.countDistinctDaysActiveAfter(userId, after);
    }
}
