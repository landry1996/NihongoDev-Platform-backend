package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.BadgeCategory;
import com.nihongodev.platform.domain.model.BadgeRarity;
import com.nihongodev.platform.domain.model.ModuleType;

import java.util.UUID;

public record BadgeDto(
    UUID id,
    String code,
    String nameJp,
    String nameEn,
    String descriptionJp,
    String descriptionEn,
    String iconUrl,
    BadgeCategory category,
    BadgeRarity rarity,
    ModuleType relatedModule,
    int xpReward
) {}
