package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.LearningActivity;
import com.nihongodev.platform.infrastructure.persistence.entity.LearningActivityEntity;
import org.springframework.stereotype.Component;

@Component
public class LearningActivityPersistenceMapper {

    public LearningActivity toDomain(LearningActivityEntity entity) {
        if (entity == null) return null;
        LearningActivity a = new LearningActivity();
        a.setId(entity.getId());
        a.setUserId(entity.getUserId());
        a.setActivityType(ActivityType.valueOf(entity.getActivityType()));
        a.setReferenceId(entity.getReferenceId());
        a.setScore(entity.getScore());
        a.setXpEarned(entity.getXpEarned());
        a.setMetadata(entity.getMetadata());
        a.setOccurredAt(entity.getOccurredAt());
        return a;
    }

    public LearningActivityEntity toEntity(LearningActivity a) {
        if (a == null) return null;
        LearningActivityEntity entity = new LearningActivityEntity();
        entity.setId(a.getId());
        entity.setUserId(a.getUserId());
        entity.setActivityType(a.getActivityType() != null ? a.getActivityType().name() : null);
        entity.setReferenceId(a.getReferenceId());
        entity.setScore(a.getScore());
        entity.setXpEarned(a.getXpEarned());
        entity.setMetadata(a.getMetadata());
        entity.setOccurredAt(a.getOccurredAt());
        return entity;
    }
}
