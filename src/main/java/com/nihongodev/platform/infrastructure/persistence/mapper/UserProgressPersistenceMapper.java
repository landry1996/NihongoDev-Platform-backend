package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.ProgressLevel;
import com.nihongodev.platform.domain.model.UserProgress;
import com.nihongodev.platform.infrastructure.persistence.entity.UserProgressEntity;
import org.springframework.stereotype.Component;

@Component
public class UserProgressPersistenceMapper {

    public UserProgress toDomain(UserProgressEntity entity) {
        if (entity == null) return null;
        UserProgress p = new UserProgress();
        p.setId(entity.getId());
        p.setUserId(entity.getUserId());
        p.setTotalLessonsCompleted(entity.getTotalLessonsCompleted());
        p.setTotalQuizzesCompleted(entity.getTotalQuizzesCompleted());
        p.setTotalInterviewsCompleted(entity.getTotalInterviewsCompleted());
        p.setTotalCorrectionsCompleted(entity.getTotalCorrectionsCompleted());
        p.setGlobalScore(entity.getGlobalScore());
        p.setCurrentStreak(entity.getCurrentStreak());
        p.setLongestStreak(entity.getLongestStreak());
        p.setLastActivityAt(entity.getLastActivityAt());
        p.setLevel(entity.getLevel() != null ? ProgressLevel.valueOf(entity.getLevel()) : ProgressLevel.BEGINNER);
        p.setTotalXp(entity.getTotalXp());
        p.setScoredActivitiesCount(entity.getScoredActivitiesCount());
        p.setWeightedScoreSum(entity.getWeightedScoreSum());
        p.setWeightSum(entity.getWeightSum());
        p.setCreatedAt(entity.getCreatedAt());
        p.setUpdatedAt(entity.getUpdatedAt());
        return p;
    }

    public UserProgressEntity toEntity(UserProgress p) {
        if (p == null) return null;
        UserProgressEntity entity = new UserProgressEntity();
        entity.setId(p.getId());
        entity.setUserId(p.getUserId());
        entity.setTotalLessonsCompleted(p.getTotalLessonsCompleted());
        entity.setTotalQuizzesCompleted(p.getTotalQuizzesCompleted());
        entity.setTotalInterviewsCompleted(p.getTotalInterviewsCompleted());
        entity.setTotalCorrectionsCompleted(p.getTotalCorrectionsCompleted());
        entity.setGlobalScore(p.getGlobalScore());
        entity.setCurrentStreak(p.getCurrentStreak());
        entity.setLongestStreak(p.getLongestStreak());
        entity.setLastActivityAt(p.getLastActivityAt());
        entity.setLevel(p.getLevel() != null ? p.getLevel().name() : "BEGINNER");
        entity.setTotalXp(p.getTotalXp());
        entity.setScoredActivitiesCount(p.getScoredActivitiesCount());
        entity.setWeightedScoreSum(p.getWeightedScoreSum());
        entity.setWeightSum(p.getWeightSum());
        entity.setCreatedAt(p.getCreatedAt());
        entity.setUpdatedAt(p.getUpdatedAt());
        return entity;
    }
}
