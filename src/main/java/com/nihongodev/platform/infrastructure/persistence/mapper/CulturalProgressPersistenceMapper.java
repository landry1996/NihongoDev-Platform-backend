package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.CulturalProgress;
import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.ScenarioCategory;
import com.nihongodev.platform.infrastructure.persistence.entity.CulturalProgressEntity;
import org.springframework.stereotype.Component;

@Component
public class CulturalProgressPersistenceMapper {

    public CulturalProgress toDomain(CulturalProgressEntity entity) {
        if (entity == null) return null;
        CulturalProgress progress = new CulturalProgress();
        progress.setId(entity.getId());
        progress.setUserId(entity.getUserId());
        progress.setCategory(entity.getCategory() != null ? ScenarioCategory.valueOf(entity.getCategory()) : null);
        progress.setScenariosCompleted(entity.getScenariosCompleted());
        progress.setTotalScore(entity.getTotalScore());
        progress.setAverageScore(entity.getAverageScore());
        progress.setBestScore(entity.getBestScore());
        progress.setCurrentStreak(entity.getCurrentStreak());
        progress.setUnlockedLevel(entity.getUnlockedLevel() != null ? KeigoLevel.valueOf(entity.getUnlockedLevel()) : null);
        progress.setLastActivityAt(entity.getLastActivityAt());
        return progress;
    }

    public CulturalProgressEntity toEntity(CulturalProgress domain) {
        if (domain == null) return null;
        CulturalProgressEntity entity = new CulturalProgressEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setCategory(domain.getCategory() != null ? domain.getCategory().name() : null);
        entity.setScenariosCompleted(domain.getScenariosCompleted());
        entity.setTotalScore(domain.getTotalScore());
        entity.setAverageScore(domain.getAverageScore());
        entity.setBestScore(domain.getBestScore());
        entity.setCurrentStreak(domain.getCurrentStreak());
        entity.setUnlockedLevel(domain.getUnlockedLevel() != null ? domain.getUnlockedLevel().name() : null);
        entity.setLastActivityAt(domain.getLastActivityAt());
        return entity;
    }
}
