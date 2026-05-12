package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.Badge;
import com.nihongodev.platform.domain.model.BadgeCategory;
import com.nihongodev.platform.domain.model.BadgeRarity;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.infrastructure.persistence.entity.BadgeEntity;
import org.springframework.stereotype.Component;

@Component
public class BadgePersistenceMapper {

    public Badge toDomain(BadgeEntity entity) {
        Badge badge = new Badge();
        badge.setId(entity.getId());
        badge.setCode(entity.getCode());
        badge.setNameJp(entity.getNameJp());
        badge.setNameEn(entity.getNameEn());
        badge.setDescriptionJp(entity.getDescriptionJp());
        badge.setDescriptionEn(entity.getDescriptionEn());
        badge.setIconUrl(entity.getIconUrl());
        badge.setCategory(BadgeCategory.valueOf(entity.getCategory()));
        badge.setRarity(BadgeRarity.valueOf(entity.getRarity()));
        if (entity.getRelatedModule() != null) {
            badge.setRelatedModule(ModuleType.valueOf(entity.getRelatedModule()));
        }
        badge.setRequiredScore(entity.getRequiredScore());
        badge.setRequiredCount(entity.getRequiredCount());
        badge.setXpReward(entity.getXpReward());
        return badge;
    }

    public BadgeEntity toEntity(Badge badge) {
        BadgeEntity entity = new BadgeEntity();
        entity.setId(badge.getId());
        entity.setCode(badge.getCode());
        entity.setNameJp(badge.getNameJp());
        entity.setNameEn(badge.getNameEn());
        entity.setDescriptionJp(badge.getDescriptionJp());
        entity.setDescriptionEn(badge.getDescriptionEn());
        entity.setIconUrl(badge.getIconUrl());
        entity.setCategory(badge.getCategory().name());
        entity.setRarity(badge.getRarity().name());
        if (badge.getRelatedModule() != null) {
            entity.setRelatedModule(badge.getRelatedModule().name());
        }
        entity.setRequiredScore(badge.getRequiredScore());
        entity.setRequiredCount(badge.getRequiredCount());
        entity.setXpReward(badge.getXpReward());
        return entity;
    }
}
