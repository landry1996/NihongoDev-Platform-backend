package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleStatus;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.infrastructure.persistence.entity.ModuleProgressEntity;
import org.springframework.stereotype.Component;

@Component
public class ModuleProgressPersistenceMapper {

    public ModuleProgress toDomain(ModuleProgressEntity entity) {
        if (entity == null) return null;
        ModuleProgress mp = new ModuleProgress();
        mp.setId(entity.getId());
        mp.setUserId(entity.getUserId());
        mp.setModuleType(ModuleType.valueOf(entity.getModuleType()));
        mp.setCompletedItems(entity.getCompletedItems());
        mp.setTotalItems(entity.getTotalItems());
        mp.setAverageScore(entity.getAverageScore());
        mp.setBestScore(entity.getBestScore());
        mp.setLastCompletedAt(entity.getLastCompletedAt());
        mp.setStatus(entity.getStatus() != null ? ModuleStatus.valueOf(entity.getStatus()) : ModuleStatus.NOT_STARTED);
        return mp;
    }

    public ModuleProgressEntity toEntity(ModuleProgress mp) {
        if (mp == null) return null;
        ModuleProgressEntity entity = new ModuleProgressEntity();
        entity.setId(mp.getId());
        entity.setUserId(mp.getUserId());
        entity.setModuleType(mp.getModuleType() != null ? mp.getModuleType().name() : null);
        entity.setCompletedItems(mp.getCompletedItems());
        entity.setTotalItems(mp.getTotalItems());
        entity.setAverageScore(mp.getAverageScore());
        entity.setBestScore(mp.getBestScore());
        entity.setLastCompletedAt(mp.getLastCompletedAt());
        entity.setStatus(mp.getStatus() != null ? mp.getStatus().name() : "NOT_STARTED");
        return entity;
    }
}
